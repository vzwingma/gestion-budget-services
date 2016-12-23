#/bin/bash
# Ce fichier doit être déposé sur OpenShift 
# ici : ~/app-root/repo/.openshift/cron/minutely/last-version-deploy.sh
PROJET_GITHUB=vzwingma/gestion-budget
VERSION=`curl -Ls -w %{url_effective} https://api.github.com/repos/$PROJET_GITHUB/releases/tags/snapshot | grep "\"published_at\":" |cut -d'"' -f4`

VERSION_PREC=`cat ~/app-root/data/.snapversion.file`

LOG=~/app-root/data/cron.log
echo "`date -u`" >> $LOG;
echo "***********************************" >> $LOG;
echo "  Version Snapshot précédente : $VERSION_PREC" >> $LOG;
echo "  Version Snapshot GitHub     : $VERSION" >> $LOG;
echo "***********************************" >> $LOG;
if [ "$VERSION_PREC" == "$VERSION" ]
then
        echo "Pas de nouvelle version détectée" >> $LOG;
else
        echo "Déploiement de la version Snapshot du $VERSION" >> $LOG;
	echo "" >> $LOG;
        cd ~/app-root/data
	./deploy.sh snapshot >> $LOG;
	RESULTAT=$?;
	#Déplacement de la cron task
	cp $OPENSHIFT_DATA_DIR/snapshot-version-deploy.sh $OPENSHIFT_REPO_DIR/.openshift/cron/minutely
	echo "" >> $LOG;
        echo "> Résultat du déploiement " $RESULTAT >> $LOG;
        if [ "$RESULTAT" == "0" ]
        then
        	echo "   La version s'est bien déployée. Mise à jour du flag" >> $LOG;
        	echo $VERSION > .snapversion.file
        else
        	echo "   La version ne s'est pas bien déployée. Tentative dans 1 minute" >> $LOG;
        fi
fi
echo "***********************************" >> $LOG;
