if [ ! -e target/site/jacoco/index.html ]
then
    bash src/test/package.sh
fi
firefox target/site/jacoco/index.html &