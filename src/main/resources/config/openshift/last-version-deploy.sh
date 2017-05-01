#/bin/bash
# Ce fichier doit être déposé sur OpenShift 
# ici : ~/app-root/repo/.openshift/cron/minutely/last-version-deploy.sh
PROJET_GITHUB=https://github.com/vzwingma/gestion-budget
VERSION=`curl -Ls -o /dev/null -w %{url_effective} $PROJET_GITHUB/releases/latest/ |cut -d'/' -f8`

VERSION_PREC=`cat ~/app-root/data/.lastversion.file`

LOG=~/app-root/data/cron.log
echo "`date -u`" > $LOG;
echo "***********************************" >> $LOG;
echo "  Version précédente : $VERSION_PREC" >> $LOG;
echo "  Version GitHub     : $VERSION" >> $LOG;
echo "***********************************" >> $LOG;
if [ "$VERSION_PREC" == "$VERSION" ] || [ "snapshot" == "$VERSION" ]
then
        echo "Pas de nouvelle version détectée" >> $LOG;
else
        echo "Déploiement de la version $VERSION" >> $LOG;
		echo "" >> $LOG;
        cd ~/app-root/data
        ./deploy.sh $VERSION >> $LOG;
        RESULTAT=$?;
		#Déplacement de la cron task
		cp $OPENSHIFT_DATA_DIR/last-version-deploy.sh $OPENSHIFT_REPO_DIR/.openshift/cron/minutely
		echo "" >> $LOG;
        echo "> Résultat du déploiement " $RESULTAT >> $LOG;
        if [ "$RESULTAT" == "0" ]
        then
        	echo "   La version s'est bien déployée" >> $LOG;
        	echo $VERSION > .lastversion.file
        else
        	echo "   La version ne s'est pas bien déployée. Tentative dans 1 minute" >> $LOG;
        fi
fi
echo "***********************************" >> $LOG;