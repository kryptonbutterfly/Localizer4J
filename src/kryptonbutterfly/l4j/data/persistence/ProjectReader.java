package kryptonbutterfly.l4j.data.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import kryptonbutterfly.java.lexer.Lexer;
import kryptonbutterfly.java.lexer.tokens.CharLiteral;
import kryptonbutterfly.java.lexer.tokens.Comment;
import kryptonbutterfly.java.lexer.tokens.Section;
import kryptonbutterfly.java.lexer.tokens.StringLiteral;
import kryptonbutterfly.l4j.data.index.ProjectStringsIndex;
import kryptonbutterfly.l4j.misc.Globals;
import kryptonbutterfly.l4j.prefs.FileSettings;
import kryptonbutterfly.l4j.util.Constants;

public class ProjectReader implements Constants
{
	private static final Lexer LEXER = new Lexer();
	
	public static ArrayList<File> findSourceFiles(File path)
	{
		try
		{
			FileVisitor visitor = new FileVisitor("", Constants.JAVA_SOURCE_EXTENSION);
			Files.walkFileTree(path.toPath(), visitor);
			return visitor.results;
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	public static ArrayList<File> findSourceFiles(String path)
	{
		try
		{
			FileVisitor visitor = new FileVisitor("", Constants.JAVA_SOURCE_EXTENSION);
			Files.walkFileTree(Paths.get(path), visitor);
			return visitor.results;
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	private static class FileVisitor extends SimpleFileVisitor<Path>
	{
		private final ArrayList<File>	results	= new ArrayList<>();
		private final String[]			fileExtension;
		private final String			path;
		
		FileVisitor(String path, String... fileExtension)
		{
			this.fileExtension	= fileExtension;
			this.path			= path;
		}
		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
		{
			if (file.toString().startsWith(path))
				for (String ext : fileExtension)
					if (file.toString().endsWith(ext))
					{
						results.add(file.toFile());
						break;
					}
			return FileVisitResult.CONTINUE;
		}
	}
	
	private static class FileExclusionVisitor extends SimpleFileVisitor<Path>
	{
		private final ArrayList<File>	results	= new ArrayList<>();
		private final String[]			fileExtensions;
		private final String			path;
		
		FileExclusionVisitor(String path, String... fileExtensions)
		{
			this.fileExtensions	= fileExtensions;
			this.path			= path;
		}
		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
		{
			if (file.toString().startsWith(path))
			{
				for (String ext : fileExtensions)
					if (file.toString().endsWith(ext))
						return FileVisitResult.CONTINUE;
					
				File f = file.toFile();
				if (!f.isDirectory())
					results.add(f);
			}
			return FileVisitResult.CONTINUE;
		}
	}
	
	public static void scan(List<File> content, String path, ExecutorService executor, ProjectStringsIndex indexes)
	{
		for (File file : content)
			executor.execute(() -> scanFile(file, path, indexes));
	}
	
	public static void loadMisc(
		ArrayList<File> miscFiles,
		String path,
		ExecutorService executor,
		ProjectStringsIndex misc)
	{
		for (File miscFile : miscFiles)
			executor.execute(() -> {
				try
				{
					loadMiscFile(miscFile, path, misc);
				}
				catch (IOException e)
				{
					System.err.println(e.getClass().getName() + e.getMessage());
				}
			});
	}
	
	private static void loadMiscFile(File miscFile, String path, ProjectStringsIndex misc) throws IOException
	{
		String fileName = miscFile.getAbsolutePath().replace(path, "");
		
		int currentLine = 1;
		
		FileReader		fr	= new FileReader(miscFile);
		BufferedReader	br	= new BufferedReader(fr);
		StringBuilder	sb	= null;
		String			line;
		while ((line = br.readLine()) != null)
		{
			int	lastIndex	= 0;
			int	index		= line.indexOf('"');
			if (index != -1)
			{
				sb			= new StringBuilder();
				lastIndex	= index;
				index		= line.indexOf('"', index + 1);
				if (index != -1)
					while (index != -1)
					{
						sb.append(line.substring(lastIndex + 1, index));
						misc.put(sb.toString(), fileName, line, currentLine);
						
						lastIndex	= index;
						index		= line.indexOf('"', index + 1);
					}
			}
			currentLine++;
		}
		br.close();
		fr.close();
	}
	
	private static void scanFile(File file, String path, ProjectStringsIndex indices)
	{
		final String className = file.getAbsolutePath().replace(path, "");
		try
		{
			final String text = Files.readString(file.toPath());
			LEXER.readFile(text, file.getPath())
				.stream()
				.filter(ProjectReader::isStringOrComment)
				.forEach(section ->
				{
					final String content;
					if (section instanceof CharLiteral cLiteral)
						content = cLiteral.value().toString();
					else if (section instanceof Comment comment)
						content = comment.value();
					else if (section instanceof StringLiteral sLiteral)
						content = sLiteral.value();
					else
					{
						final var type = section.getClass().getName();
						throw new IllegalStateException("Unexpected section of type %s.".formatted(type));
					}
					synchronized (indices)
					{
						indices.put(content, className, null, section.loc().line());
					}
				});
		}
		catch (IllegalStateException | IOException e)
		{
			System.err
				.println(e.getMessage() + " " + file.getAbsolutePath().replace(new File(path).getAbsolutePath(), ""));
		}
	}
	
	private static boolean isStringOrComment(Section<?> section)
	{
		return section instanceof CharLiteral
				|| section instanceof Comment
				|| section instanceof StringLiteral;
	}
	
	public static ArrayList<File> findMiscFiles(File path, FileSettings settings)
	{
		try
		{
			final var	extension	= settings.languageFileType.extension(settings);
			final var	visitor		= new FileExclusionVisitor(path.toString(), extension, JAVA_SOURCE_EXTENSION);
			Files.walkFileTree(path.toPath(), visitor);
			return visitor.results;
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	public static ArrayList<File> findMiscFiles(String path)
	{
		return findMiscFiles(new File(path), Globals.prefs.fileSettings);
	}
}