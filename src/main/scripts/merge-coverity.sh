#/bin/bash
echo ${TRAVIS_BRANCH}
# Tag de snapshot
if [ ${TRAVIS_BRANCH} = "snapshot" ]; then
	echo "Merge vers la branche Coverity";
else
	echo "Pas de merge vers la branche Coverity";
fi