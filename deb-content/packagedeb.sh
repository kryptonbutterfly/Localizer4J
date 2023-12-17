#!/bin/bash

export SUDO_ASKPASS="/opt/custom_commands/zenityPw.sh"

if [ "$(id -u)" != "0" ]; then
	exec sudo "$0" "$@"
fi

cd ./deb-content
echo ${pwd}

VERSION_FILE="version.txt"

APP_NAME="Localizer4J"
ARTIFACT_NAME="localizer4j"
PACKAGE_NAME=$APP_NAME
PACKAGE_VERSION=$(<$VERSION_FILE) 
SOURCE_DIR=$PWD/..
DEPLOY_DIR="/home/$SUDO_USER/ownCloud/Applications"
TEMP_DIR="/tmp"

mkdir -p $TEMP_DIR/debian/DEBIAN
mkdir -p $TEMP_DIR/debian/usr/share/applications
mkdir -p $TEMP_DIR/debian/usr/share/mime
mkdir -p $TEMP_DIR/debian/usr/share/mime/packages
mkdir -p $TEMP_DIR/debian/usr/share/$PACKAGE_NAME
mkdir -p $TEMP_DIR/debian/usr/share/doc/$PACKAGE_NAME
mkdir -p $TEMP_DIR/debian/usr/share/common-licenses/$PACKAGE_NAME

echo "Package: $PACKAGE_NAME" > $TEMP_DIR/debian/DEBIAN/control
echo "Version: $PACKAGE_VERSION" >> $TEMP_DIR/debian/DEBIAN/control
cat control >> $TEMP_DIR/debian/DEBIAN/control

cp $APP_NAME.desktop $TEMP_DIR/debian/usr/share/applications/
cp langProject.xml $TEMP_DIR/debian/usr/share/mime/packages/

cp $SOURCE_DIR/target/$ARTIFACT_NAME-$PACKAGE_VERSION.jar $TEMP_DIR/debian/usr/share/$PACKAGE_NAME/$APP_NAME.jar

echo "$PACKAGE_NAME ($PACKAGE_VERSION) trusty; urgency=low" > changelog
echo "  * Rebuild" >> changelog
echo " -- tinycodecrank <tinycodecrank@gmail.com> `date -R`" >> changelog
gzip -9c changelog > $TEMP_DIR/debian/usr/share/doc/$PACKAGE_NAME/changelog.gz

cp *.svg $TEMP_DIR/debian/usr/share/$PACKAGE_NAME/
cp *.png $TEMP_DIR/debian/usr/share/$PACKAGE_NAME/

chmod 0664 $TEMP_DIR/debian/usr/share/$PACKAGE_NAME/*svg
chmod 0664 $TEMP_DIR/debian/usr/share/$PACKAGE_NAME/*png

PACKAGE_SIZE=`du -bs $TEMP_DIR/debian | cut -f 1`
PACKAGE_SIZE=$((PACKAGE_SIZE/1024))
echo "Installed-Size: $PACKAGE_SIZE" >> $TEMP_DIR/debian/DEBIAN/control

chown -R root $TEMP_DIR/debian/
chgrp -R root $TEMP_DIR/debian/

cd $TEMP_DIR/
dpkg --build debian

mkdir -p $SOURCE_DIR/build
mv $TEMP_DIR/debian.deb $SOURCE_DIR/build/$PACKAGE_NAME-$PACKAGE_VERSION.deb
rm -r $TEMP_DIR/debian

cp $SOURCE_DIR/build/$PACKAGE_NAME-$PACKAGE_VERSION.deb $DEPLOY_DIR/Deb/
cp $SOURCE_DIR/target/$ARTIFACT_NAME-$PACKAGE_VERSION.jar $DEPLOY_DIR/
