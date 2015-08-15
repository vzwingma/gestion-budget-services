#/bin/bash
#Verification du nombre d'arguments
if [ $# -ne 3 ]        
then                   
    echo -e "\nErreur : Il manque des paramètres obligatoires"
	echo -e "- le paramètre environnement {dev/prod}."
	echo -e "- la version applicative"
	echo -e "- le numéro de build"
    exit 1
fi

export ENV=$1
export VERSION=$2
export BUILD=$3

wget https://s3-eu-west-1.amazonaws.com/gestion-budget/artifacts/$BUILD/target/Budget-v$VERSION.tar.gz


tar xvzf *.tar.gz
mv $OPENSHIFT_DATA_DIR/ROOT.war $OPENSHIFT_JBOSSEWS_DIR/webapps/ROOT.war
cp $OPENSHIFT_DATA_DIR/context.$ENV.xml $OPENSHIFT_JBOSSEWS_DIR/conf
mv *.tar.gz old/
