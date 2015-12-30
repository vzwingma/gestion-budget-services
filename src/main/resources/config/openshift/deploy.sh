#/bin/bash
#Verification du nombre d'arguments
if [ $# -ne 1 ]
then                   
    echo -e "\nErreur : Il manque des paramètres obligatoires"
	echo -e "- la version applicative"
    exit 1
fi

export VERSION=$1

wget https://github.com/vzwingma/gestion-budget/releases/download/$VERSION/Budget.tar.gz
tar xvzf *.tar.gz
mv $OPENSHIFT_DATA_DIR/ROOT.war $OPENSHIFT_JBOSSEWS_DIR/webapps/ROOT.war
rm *.tar.gz
