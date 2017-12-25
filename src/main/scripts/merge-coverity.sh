#/bin/bash
echo ${TRAVIS_BRANCH}
# Merge vers la branche coverity_scan
if [ ${TRAVIS_BRANCH} = "snapshot" ]; then
	echo "Merge vers la branche Coverity";
	git branch coverity_scan
	git checkout coverity_scan
	git merge master
	cp src/main/external-ressources/template/coverity.travis.yml .travis.yml
	git add .travis.yml
	git commit -m "Application du .travis.yml Coverity"
	git config --global push.default simple
	git push https://$GITHUB_API_KEY@github.com/vzwingma/gestion-budget --set-upstream origin coverity_scan 
else
	echo "Pas de merge vers la branche Coverity";
fi