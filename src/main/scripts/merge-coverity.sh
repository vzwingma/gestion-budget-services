#/bin/bash
echo ${TRAVIS_BRANCH}
# Merge vers la branche coverity_scan
if [ ${TRAVIS_BRANCH} = "snapshot" ]; then
	echo "Merge vers la branche Coverity";
else
	echo "Pas de merge vers la branche Coverity";
fi