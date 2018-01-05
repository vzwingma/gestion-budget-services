#/bin/bash
echo ${TRAVIS_BRANCH}
# Merge vers la branche coverity_scan
if [ ${TRAVIS_BRANCH} = "snapshot" ]; then
	echo "Merge vers la branche Coverity";
	git fetch --all
	git checkout coverity_scan
	git rebase master
	git config --global push.default simple
	git push https://$GITHUB_API_KEY@github.com/vzwingma/gestion-budget --set-upstream origin coverity_scan 
else
	echo "Pas de merge vers la branche Coverity";
fi