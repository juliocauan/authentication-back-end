if [ ! -e target/site/jacoco/index.html ]
then
    bash scripts/test.sh
fi
firefox target/site/jacoco/index.html &