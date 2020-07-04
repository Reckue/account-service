gradle build --info

bash ./config/docker/create_image.sh
bash ./config/docker/push_image.sh

# autodeploy
oc apply -f ./config/k8s/deploy.yaml
