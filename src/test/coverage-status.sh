if [ ! -e target/site/jacoco/index.html ]
then
    bash src/test/test-and-package.sh
fi
firefox target/site/jacoco/index.html &