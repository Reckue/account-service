gradle build --info

bash ./config/docker/create_image.sh
bash ./config/docker/push_image.sh

# autodeploy
oc delete -f ./config/k8s/deployment.yml
oc create -f ./config/k8s/deployment.yml

oc delete -f ./config/k8s/service.yml
oc create -f ./config/k8s/service.yml

oc delete -f ./config/k8s/route.yml
oc create -f ./config/k8s/route.yml
