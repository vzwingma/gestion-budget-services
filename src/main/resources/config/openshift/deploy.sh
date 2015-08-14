#/bin/bash
#Verification du nombre d'arguments
if [ $# -ne 1 ]        
then                   
    echo -e "\nErreur : Il manque le paramètre environnement {dev/prod}."
    exit 1
fi

ENV = $1
tar xvzf *.tar.gz
mv $OPENSHIFT_DATA_DIR/ROOT.war $OPENSHIFT_JBOSSEWS_DIR/webapps/ROOT.war
cp $OPENSHIFT_DATA_DIR/context.$ENV.xml $OPENSHIFT_JBOSSEWS_DIR/conf
mv *.tar.gz old/
