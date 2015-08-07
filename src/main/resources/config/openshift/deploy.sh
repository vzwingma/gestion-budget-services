#/bin/bash
tar xvzf *.tar.gz
mv $OPENSHIFT_DATA_DIR/ROOT.war $OPENSHIFT_JBOSSEWS_DIR/webapps/ROOT.war
mv $OPENSHIFT_DATA_DIR/*.xml $OPENSHIFT_JBOSSEWS_DIR/conf
mv *.tar.gz old/
