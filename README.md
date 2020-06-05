# backbase-operator project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `bb-operator-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/bb-operator-1.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/bb-operator-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## Testing

By default, Pods in Kubernetes do not have the permission to list other pods. Therefore, we need to create a cluster role, a service account, and a cluster role binding.

kubectl apply -f k8s_files/operator.clusterrole.yaml
kubectl apply -f k8s_files/operator.serviceaccount.yaml
kubectl apply -f k8s_files/operator.clusterrolebinding.yaml

Now you can run the `kubectl apply -f k8s_files/operator.crd.yaml` command to register the CRD in the cluster. 

Run the `kubectl apply -f k8s_files/operator.deployment.yaml` command to register the operator.


### Running the example
Apply the custom resource by running: kubectl apply -f cx-statics.yaml and check the output of kubectl get pods command.

```
│ exec java -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -XX:+UseParallelGC -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=40 -XX:+ExitOnOutOfMem │
│ oryError -cp . -jar /deployments/app.jar                                                                                                                                                                                                   │
│ Startup                                                                                                                                                                                                                                    │
│ Event ADDED                                                                                                                                                                                                                                │
│ __  ____  __  _____   ___  __ ____  ______                                                                                                                                                                                                 │
│  --/ __ \/ / / / _ | / _ \/ //_/ / / / __/                                                                                                                                                                                                 │
│  -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \                                                                                                                                                                                                   │
│ --\___\_\____/_/ |_/_/|_/_/|_|\____/___/                                                                                                                                                                                                   │
│ 2020-06-05 13:28:17,715 INFO  [io.quarkus] (main) bb-operator 1.0-SNAPSHOT (powered by Quarkus 1.4.2.Final) started in 2.832s. Listening on: http://0.0.0.0:8080                                                                           │
│ 2020-06-05 13:28:17,719 INFO  [io.quarkus] (main) Profile prod activated.                                                                                                                                                                  │
│ 2020-06-05 13:28:17,719 INFO  [io.quarkus] (main) Installed features: [cdi, kubernetes, kubernetes-client, resteasy]                                                                                                                       │
│ 200                                                                                                                                                                                                                                        │
│ 04497dab-7f49-4719-b9bf-059c82a4ada8                                                                                                                                                                                                       │
│ eyJraWQiOiJNYmV1VmVVWlhVT2FJcDgwYmx1XC9sanFOQjNKZE9aSDgxQ3JGU0tpMmVcL2M9IiwiY3R5IjoiSldUIiwiZW5jIjoiQTEyOENCQy1IUzI1NiIsImFsZyI6ImRpciJ9..FJoUzfEHkhNNzHxEEOO_Yw.DIyxYrqALdJhHx3HPF-xJDRGulnT8tGb7v0IQLfEhrXtySZme5gI7b5AQ2BlfjaCkmt9rwAJX │
│ 0gxcDJPyEvJNnc-NyjwMRIUNGa8bEmoTVhOxasjAH04-6KNLb0K16C4txp_qqfyV1NLQTPf4B0ltHkATxvMXSAGvpByqpCBYgF6DC1p2ftpKoewLfiKdHPhcmw1yD28mrhd7toxiVttHJCbVxV8HcZjfDClCvGrNhqa0lLd2XKipE6VvxsiTfqGpQtusha2BSVpPdQOXbu9GHawHBmP_b8ADaRJKDLeMXP6bsUVfAd │
│ VyWVBCi_n2S37z_G5FMDAOW5kZMzeILx6xYcBkcuZ42zx8Z3QSU5bevDZHHncroDWS2L5o-a19xW24mKdkREmU0c1J6-sEeEAxkWDagaEMdvoxoy-IAJxkr4DUzDT7KxTrzdQBG7iZL34l1hudo0ftMNUTRJWI4xCbtJ-YaKZIW9L323EABPL_FBwMjXC7HU1RQa-j43D54_GHbtFGHYdXAlSvYU1L4BtFXwfRWwUU │
│ l4xz6Nb5A4kSV_akhqgafKYqmC7ADIEoRCnOK0v9VzrFeZwZYuCk8ITbw.v9F13ZOBvIgh0M3i294ULw                                                                                                                                                           │
│ 201
```

