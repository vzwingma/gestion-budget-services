#/bin/bash
#Verification du nombre d'arguments
if [ $# -ne 2 ]        
then                   
    echo -e "\nErreur : Il manque des paramètres obligatoires"
	echo -e "- la version applicative"
	echo -e "- le numéro de build"
    exit 1
fi

export VERSION=$1
export BUILD=$2

wget https://s3-eu-west-1.amazonaws.com/gestion-budget/artifacts/$BUILD/target/Budget-v$VERSION.tar.gz


tar xvzf *.tar.gz
mv $OPENSHIFT_DATA_DIR/ROOT.war $OPENSHIFT_JBOSSEWS_DIR/webapps/ROOT.war
rm *.tar.gz
