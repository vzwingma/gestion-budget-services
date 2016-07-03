#/bin/bash
# Ce fichier doit être déposé sur OpenShift 
# ici : ~/app-root/repo/.openshift/cron/minutely/last-version-deploy.sh
PROJET_GITHUB=https://github.com/vzwingma/gestion-budget
VERSION=`curl -Ls -o /dev/null -w %{url_effective} $PROJET_GITHUB/releases/latest/ |cut -d'/' -f8`

VERSION_PREC=`cat ~/app-root/data/.lastversion.file`

LOG=~/app-root/data/cron.log
echo "`date -u`" > $LOG;
echo "Version précédente : $VERSION_PREC" >> $LOG;
echo "Version GitHub     : $VERSION" >> $LOG;

if [ "$VERSION_PREC" == "$VERSION" ]
then
        echo "Pas de nouvelle version détectée" >> $LOG;
else
        echo "Déploiement de la version $VERSION" >> $LOG;
        cd ~/app-root/data
        ./deploy.sh $VERSION >> $LOG;
        echo $VERSION > .lastversion.file
fi