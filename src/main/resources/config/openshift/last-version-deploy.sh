#/bin/bash
PROJET_GITHUB=https://github.com/vzwingma/gestion-budget
VERSION=`curl -Ls -o /dev/null -w %{url_effective} $PROJET_GITHUB/releases/latest/ |cut -d'/' -f8`

echo "Déploiement de la version $VERSION"
./deploy.sh $VERSION
