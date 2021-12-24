# checkinout

This project can be used as a starting point to create your own Vaadin application with Spring Boot. It contains all the
necessary configuration and some placeholder files to get you started.

## Running the application

The project is a standard Maven project. To run it from the command line, type `mvnw` (Windows), or `./mvnw` (Mac &
Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any Maven project. Read more
on [how to set up a development environment for Vaadin projects](https://vaadin.com/docs/latest/guide/install) (Windows,
Linux, macOS).

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows), or `./mvnw clean package -Pproduction` (
Mac & Linux). This will build a JAR file with all the dependencies and front-end resources, ready to be deployed. The
file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/checkinout-1.0-SNAPSHOT.jar`

## Project structure

- `MainView.java` in `src/main/java` contains the navigation setup (i.e., the side/top bar and the main menu). This
  setup uses
  [App Layout](https://vaadin.com/components/vaadin-app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/` contains the client-side JavaScript views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorials at [vaadin.com/tutorials](https://vaadin.com/tutorials).
- Watch training videos and get certified at [vaadin.com/learn/training](https://vaadin.com/learn/training).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/components](https://vaadin.com/components).
- Discover Vaadin's set of CSS utility classes that enable building any UI without custom CSS in
  the [docs](https://vaadin.com/docs/latest/ds/foundation/utility-classes).
- Find a collection of solutions to common use cases in [Vaadin Cookbook](https://cookbook.vaadin.com/).
- Find Add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join
  our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin/platform).

## Deploying using Docker

To build the Dockerized version of the project, run

```
docker build . -t yilmazchef/checkinout:latest
```

Once the Docker image is correctly built, you can test it locally using

```
docker run -p 8443:443 yilmazchef/checkinout:latest
```

## Deploying using Kubernetes

We assume here that you have the Kubernetes cluster from Docker Desktop running (can be enabled in the settings).

First build the Docker image for your application. You then need to make the Docker image available to you cluster. With
Docker Desktop Kubernetes, this happens automatically. With Minikube, you can run `eval $(minikube docker-env)` and then
build the image to make it available. For other clusters, you need to publish to a Docker repository or check the
documentation for the cluster.

The included `kubernetes.yaml` sets up a deployment with 2 pods (server instances) and a load balancer service. You can
deploy the application on a Kubernetes cluster using

```
kubectl apply -f kubernetes.yaml
```

If everything works, you can access your application by opening http://localhost:8000/. If you have something else
running on port 8000, you need to change the load balancer port in `kubernetes.yaml`.

Tip: If you want to understand which pod your requests go to, you can add the value
of `VaadinServletRequest.getCurrent().getLocalAddr()` somewhere in your UI.

### Troubleshooting

If something is not working, you can try one of the following commands to see what is deployed and their status.

```
kubectl get pods
kubectl get services
kubectl get deployments
```

If the pods say `Container image "checkinout:latest" is not present with pull policy of Never` then you have not built
your application using Docker or there is a mismatch in the name. Use `docker images ls` to see which images are
available.

If you need even more information, you can run

```
kubectl cluster-info dump
```

that will probably give you too much information but might reveal the cause of a problem.

If you want to remove your whole deployment and start over, run

```
kubectl delete -f kubernetes.yaml
```

## GENERATE HTTPS KEYS

___

By default, Spring Boot application uses HTTP 8080 port when the application starts up.

![Started Application on Tomcat port_8080](https://www.tutorialspoint.com/spring_boot/images/started_application_on_tomcat_port_8080.jpg)

You need to follow the steps given below to configure the HTTPS and the port 443 in Spring Boot application −

- Obtain the SSL certificate – Create a self-signed certificate or get one from a Certificate Authority

- Enable HTTPS and 443 port

## Self-Signed Certificate

To create a self-signed certificate, Java Run Time environment comes bundled with certificate management utility key
tool. This utility tool is used to create a Self-Signed certificate. It is shown in the code given here −

keytool \-genkey \-alias tomcat \-storetype PKCS12 \-keyalg RSA \-keysize 2048 \-keystore keystore.p12 \-validity 3650
Enter keystore password: Re\-enter new password: What is your first and last name? \[Unknown\]: What is the name of your
organizational unit? \[Unknown\]: What is the name of your organization? \[Unknown\]: What is the name of your City or
Locality? \[Unknown\]: What is the name of your State or Province? \[Unknown\]: What is the two\-letter country code for
this unit? \[Unknown\]: Is CN \= Unknown, OU\=Unknown, O \= Unknown, L \= Unknown, ST \= Unknown, C \= Unknown correct?
\[no\]: yes

This code will generate a PKCS12 keystore file named as keystore.p12 and the certificate alias name is tomcat.

## Configure HTTPS

We need to provide the server port as 443, key-store file path, key-store-password, key-store-type and key alias name
into the application.properties file. Observe the code given here −

server.port: 443 server.ssl.key\-store: keystore.p12 server.ssl.key\-store\-password: springboot
server.ssl.keyStoreType: PKCS12 server.ssl.keyAlias: tomcat

You can use the following code if you are using YAML properties use below application.yml −

server: port: 443 ssl: key\-store: keystore.p12 key\-store\-password: springboot keyStoreType: PKCS12 keyAlias: tomcat

You can create an executable JAR file, and run the spring boot application by using the following Maven or Gradle
commands.

For Maven, you can use the following command −

mvn clean install

After “BUILD SUCCESS”, you can find the JAR file under the target directory.

For Gradle, you can use the command

gradle clean build

After “BUILD SUCCESSFUL”, you can find the JAR file under the build/libs directory.

Now, run the JAR file by using the following command −

java –jar <JARFILE>

Now, the application has started on the Tomcat port 443 with https.

## Docker handbook

sudo docker exec -it mysqldb bash

sudo docker exec -it webapp bash

## Building an Image

Containers have emerged as the preferred means of packaging an application with all the software and operating system
dependencies and then shipping that across to different environments.

This article looks at different ways of containerizing a Spring Boot application:

- building a Docker image using a Docker file,
- building an OCI image from source code with Cloud-Native Buildpack,
- and optimizing the image at runtime by splitting parts of the JAR into different layers using layered tools.

## [](https://reflectoring.io/spring-boot-docker/#code-example)[](https://github.com/thombergs/code-examples/tree/master/spring-boot/spring-boot-docker)Code Example

This article is accompanied by a working code
example [on GitHub](https://github.com/thombergs/code-examples/tree/master/spring-boot/spring-boot-docker).

## [](https://reflectoring.io/spring-boot-docker/#container-terminology)Container Terminology

We will start with the container terminologies used throughout the article:

- **Container image**: a file with a specific format. We convert our application into a container image by running a
  build tool.

- **Container**: the runtime instance of a container image.

- **Container engine**: the daemon process responsible for running the Container.

- **Container host**: the host machine on which the container engine runs.

- **Container registry**: the shared location that is used for publishing and distributing the container image.

- **OCI Standard**: the [Open Container Initiative (OCI)](https://opencontainers.org/about/overview/) is a lightweight,
  open governance structure formed under the Linux Foundation. The OCI Image Specification defines industry standards
  for container image formats and runtimes to ensure that all container engines can run container images produced by any
  build tool.

**To containerize an application, we enclose our application inside a container image and publish that image to a shared
registry. The container runtime pulls this image from the registry, unpacks the image, and runs the application inside
it.**

The 2.3 release of Spring Boot provides plugins for building OCI images.

[Docker](https://docs.docker.com/get-started/#docker-concepts) happens to be the most commonly used container
implementation and we are using Docker in our examples, so all subsequent reference to a container in this article will
mean Docker.

## [](https://reflectoring.io/spring-boot-docker/#building-a-container-image-the-conventional-way)Building a Container Image the Conventional Way

It is very easy to create Docker images of Spring Boot applications by adding a few instructions to a Docker file.

We first build an executable JAR and as part of the Docker file instructions, copy the executable JAR over a base JRE
image after applying necessary customizations.

Let us create our Spring Boot application
from [Spring Initializr](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.3.3.RELEASE&packaging=jar&jvmVersion=11&groupId=io.pratik.users&artifactId=usersignup&name=usersignup&description=Demo%20project%20for%20Spring%20Boot%20Container&packageName=io.pratik.users&dependencies=web,actuator,lombok)
with dependencies for `web`, `lombok`, and `actuator`. We also add a rest controller to expose an API with the `GET`
method.

### [](https://reflectoring.io/spring-boot-docker/#creating-a-docker-file)Creating a Docker File

Next, we containerize this application by adding a `Dockerfile`:

```
FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/application.jar"]
```

Our Docker file contains a base image from `adoptopenjdk` over which we copy our JAR file and then expose the
port `8080` which will listen for requests.

### [](https://reflectoring.io/spring-boot-docker/#building-the-application)Building the Application

We first build the application with Maven or Gradle. We are using Maven here:

This creates an executable JAR of the application. We need to convert this executable JAR into a Docker image for
running in a Docker engine.

### [](https://reflectoring.io/spring-boot-docker/#building-the-container-image)Building the Container Image

Next, we put this executable JAR in a Docker image by running the `docker build` command from the root project directory
containing the Docker file created earlier:

```
docker build  -t usersignup:v1 .
```

We can see our image listed with the command:

The output of the above command includes our image `usersignup` along with the base image `adoptopenjdk` specified in
our Docker file.

```
REPOSITORY          TAG                 SIZE
usersignup          v1                  249MB
adoptopenjdk        11-jre-hotspot      229MB
```

### [](https://reflectoring.io/spring-boot-docker/#viewing-the-layers-inside-the-container-image)Viewing the Layers Inside the Container Image

Let us see the stack of layers inside the image. We will use the [dive tool](https://github.com/wagoodman/dive) to view
those layers:

Here is part of the output from running the Dive
command: ![dive screenshot](https://reflectoring.io/assets/img/posts/springboot-docker-image/dive1.png)

As we can see the application layer forms a significant part of the image size. We will aim to reduce the size of this
layer in the following sections as part of our optimization.

## [](https://reflectoring.io/spring-boot-docker/#building-a-container-image-with-buildpack)Building a Container Image with Buildpack

[Buildpacks](https://buildpacks.io/) is a generic term used by various Platform as a Service(PAAS) offerings to build a
container image from source code. It was started by Heroku in 2011 and has since been adopted by Cloud Foundry, Google
App Engine, Gitlab, Knative, and some others.

![dive screenshot](https://reflectoring.io/assets/img/posts/springboot-docker-image/Docker_buildpack.png)

### [](https://reflectoring.io/spring-boot-docker/#advantage-of-cloud-native-buildpacks)Advantage of Cloud-Native Buildpacks

One main advantage of using Buildpack for building images is that **changes to the image configuration can be managed in
a centralized place (the builder) and propagated to all applications which are using the builder.**

Buildpacks were tightly coupled to the platform. **Cloud-Native Buildpacks bring standardization across platforms by
supporting the OCI image format which ensures the image can be run by a Docker engine.**

### [](https://reflectoring.io/spring-boot-docker/#using-the-spring-boot-plugin)Using the Spring Boot Plugin

The Spring Boot plugin creates OCI images from the source code using a Buildpack. Images are built using
the `bootBuildImage` task (Gradle) or the `spring-boot:build-image` goal (Maven) and a local Docker installation.

We can customize the name of the image required for pushing to the Docker Registry by specifying the name in
the `image tag`:

```
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <configuration>
    <image>
      <name>docker.io/pratikdas/${project.artifactId}:v1</name>
    </image>
  </configuration>
</plugin>
```

Let us use Maven to run the `build-image` goal to build the application and create the container image. We are not using
any Docker file now.

```
mvn spring-boot:build-image
```

Running this will produce an output similar to:

```
[INFO] --- spring-boot-maven-plugin:2.3.3.RELEASE:build-image (default-cli) @ usersignup ---
[INFO] Building image 'docker.io/pratikdas/usersignup:v1'
[INFO] 
[INFO]  > Pulling builder image 'gcr.io/paketo-buildpacks/builder:base-platform-api-0.3' 0%
.
.
.. [creator]     Adding label 'org.springframework.boot.version'
.. [creator]     *** Images (c311fe74ec73):
.. [creator]           docker.io/pratikdas/usersignup:v1
[INFO] 
[INFO] Successfully built image 'docker.io/pratikdas/usersignup:v1'
```

From the output, we can see the `paketo Cloud-Native buildpack` being used to build a runnable OCI image. As we did
earlier, we can see the image listed as a Docker image by running the command:

Output:

```
REPOSITORY                             SIZE
paketobuildpacks/run                  84.3MB
gcr.io/paketo-buildpacks/builder      652MB
pratikdas/usersignup                  257MB
```

## [](https://reflectoring.io/spring-boot-docker/#building-a-container-image-with-jib)Building a Container Image with Jib

Jib is an image builder plugin from Google and provides an alternate method of building a container image from source
code.

We configure the `jib-maven-plugin` in pom.xml:

```
      <plugin>
        <groupId>com.google.cloud.tools</groupId>
        <artifactId>jib-maven-plugin</artifactId>
        <version>2.5.2</version>
      </plugin>
```

Next, we trigger the Jib plugin with the Maven command to build the application and create the container image. As
before, we are not using any Docker file here:

```
mvn compile jib:build -Dimage=<docker registry name>/usersignup:v1
```

We get the following output after running the above Maven command:

```
[INFO] Containerizing application to pratikdas/usersignup:v1...
.
.
[INFO] Container entrypoint set to [java, -cp, /app/resources:/app/classes:/app/libs/*, io.pratik.users.UsersignupApplication]
[INFO] 
[INFO] Built and pushed image as pratikdas/usersignup:v1
[INFO] Executing tasks:
[INFO] [==============================] 100.0% complete
```

The output shows that the container image is built and pushed to the registry.

## [](https://reflectoring.io/spring-boot-docker/#motivations-and-techniques-for-building-optimized-images)Motivations and Techniques for Building Optimized Images

We have two main motivations for optimization:

- **Performance**: in a container orchestration system, the container image is pulled from the image registry to a host
  running a container engine. This process is called scheduling. Pulling large-sized images from the registry result in
  long scheduling times in container orchestration systems and long build times in CI pipelines.
- **Security**: large-sized images also have a greater surface area for vulnerabilities.

**A Docker image is composed of a stack of layers each representing an instruction in our Dockerfile.** Each layer is a
delta of the changes over the underlying layer. When we pull the Docker image from the registry, it is pulled by layers
and cached in the host.

Spring Boot uses
a [“fat JAR”](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-executable-jar-format.html#executable-jar-jar-file-structure)
as its default packaging format. When we inspect the fat JAR, we can see that the application forms a very small part of
the entire JAR. This is the part that changes most frequently. The remaining part is composed of the Spring Framework
dependencies.

The optimization formula centers around isolating the application into a separate layer from the Spring Framework
dependencies.

The dependencies layer forming the bulk of the fat JAR is downloaded only once and cached in the host system.

**Only the thin layer of application is pulled during application updates and container scheduling** as illustrated in
this diagram:

![dive screenshot](https://reflectoring.io/assets/img/posts/springboot-docker-image/Docker_optimized.png)

Let’s have a look at how to build those optimized images for a Spring Boot application in the next sections.

## [](https://reflectoring.io/spring-boot-docker/#building-an-optimized-container-image-for-a-spring-boot-application-with-buildpack)Building an Optimized Container Image for a Spring Boot Application with Buildpack

Spring Boot 2.3 supports layering by extracting parts of the fat JAR into separate layers. The layering feature is
turned off by default and needs to be explicitly enabled with the Spring Boot Maven plugin:

```
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <configuration>
    <layers>
      <enabled>true</enabled>
    </layers>
  </configuration> 
</plugin>
```

We will use this configuration to generate our container image first with Buildpack and then with Docker in the
following sections.

Let us run the Maven `build-image` goal to create the container image:

```
mvn spring-boot:build-image
```

If we run Dive to see the layers in the resulting image, we can see the application layer (encircled in red) is much
smaller in the range of kilobytes compared to what we had obtained by using the fat JAR format:

![dive screenshot](https://reflectoring.io/assets/img/posts/springboot-docker-image/dive-buildpack-layer.png)

## [](https://reflectoring.io/spring-boot-docker/#building-an-optimized-container-image-for-a-spring-boot-application-with-docker)Building an Optimized Container Image for a Spring Boot Application with Docker

Instead of using the Maven or Gradle plugin, we can also create a layered JAR Docker image with a Docker file.

When we are using Docker, we need to perform two additional steps for extracting the layers and copying those in the
final image.

The contents of the resulting JAR after building with Maven with the layering feature turned on will look like this:

```
META-INF/
.
BOOT-INF/lib/
.
BOOT-INF/lib/spring-boot-jarmode-layertools-2.3.3.RELEASE.jar
BOOT-INF/classpath.idx
BOOT-INF/layers.idx
```

The output shows an additional JAR named `spring-boot-jarmode-layertools` and a `layersfle.idx` file. The layering
feature is provided by this additional JAR as explained in the next section.

To view and extract the layers from our layered JAR, we use a system property `-Djarmode=layertools` to launch
the `spring-boot-jarmode-layertools` JAR instead of the application:

```
java -Djarmode=layertools -jar target/usersignup-0.0.1-SNAPSHOT.jar
```

Running this command produces the output containing available command options:

```
Usage:
  java -Djarmode=layertools -jar usersignup-0.0.1-SNAPSHOT.jar

Available commands:
  list     List layers from the jar that can be extracted
  extract  Extracts layers from the jar for image creation
  help     Help about any command

```

The output shows the commands `list`, `extract`, and `help` with `help` being the default. Let us run the command with
the `list` option:

```
java -Djarmode=layertools -jar target/usersignup-0.0.1-SNAPSHOT.jar list
```

```
dependencies
spring-boot-loader
snapshot-dependencies
application
```

We can see the list of dependencies that can be added as layers.

The default layers are:

Layer name

Contents

`dependencies`

any dependency whose version does not contain SNAPSHOT

`spring-boot-loader`

JAR loader classes

`snapshot-dependencies`

any dependency whose version contains SNAPSHOT

`application`

application classes and resources

The layers are defined in a `layers.idx` file in the order that they should be added to the Docker image. These layers
get cached in the host after the first pull since they do not change. **Only the updated application layer is downloaded
to the host which is faster because of the reduced size**.

We will build the final image in two stages using a method
called [multi-stage build](https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds). In
the first stage, we will extract the dependencies and in the second stage, we will copy the extracted dependencies to
the final image.

Let us modify our Docker file for multi-stage build:

```
# the first stage of our build will extract the layers
FROM adoptopenjdk:14-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# the second stage of our build will copy the extracted layers
FROM adoptopenjdk:14-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

```

We save this configuration in a separate file - `Dockerfile2`.

We build the Docker image using the command:

```
docker build -f Dockerfile2 -t usersignup:v1 .
```

After running this command, we get this output:

```
Sending build context to Docker daemon  20.41MB
Step 1/12 : FROM adoptopenjdk:14-jre-hotspot as builder
14-jre-hotspot: Pulling from library/adoptopenjdk
.
.
Successfully built a9ebf6970841
Successfully tagged userssignup:v1
```

We can see the Docker image is created with an Image ID and then tagged.

We finally run the Dive command as before to check the layers inside the generated Docker image. We can specify either
the Image ID or tag as input to the Dive command:

As we can see in the output, the layer containing the application is only 11 kB now with the dependencies cached in
separate layers. ![dive screenshot](https://reflectoring.io/assets/img/posts/springboot-docker-image/dive2.png)

We can further reduce the application layer size by extracting any of our custom dependencies in a separate layer
instead of packaging them with the application by declaring them in a `yml` like file named `layers.idx`:

```
- "dependencies":
  - "BOOT-INF/lib/"
- "spring-boot-loader":
  - "org/"
- "snapshot-dependencies":
- "custom-dependencies":
  - "io/myorg/"
- "application":
  - "BOOT-INF/classes/"
  - "BOOT-INF/classpath.idx"
  - "BOOT-INF/layers.idx"
  - "META-INF/"

```

In this file -`layers.idx` we have added a custom dependency with the name `io.myorg` containing organization
dependencies pulled from a shared repository.

## [](https://reflectoring.io/spring-boot-docker/#conclusion)Conclusion

In this article, we looked at using Cloud-Native Buildpacks to create the container image directly from source code.
This is an alternative to using Docker for building the container image using the conventional way, by first building
the fat executable JAR and then packaging it in a container image by specifying the instructions in a Dockerfile.

We also looked at optimizing our container by enabling the layering feature which extracts the dependencies in separate
layers that get cached in the host and the thin layer of application is downloaded during scheduling in container
runtime engines.

You can refer to all the source code used in the article
on [Github](https://github.com/thombergs/code-examples/tree/master/spring-boot/spring-boot-docker).

## [](https://reflectoring.io/spring-boot-docker/#command-reference)Command Reference

Here is a summary of commands which we used throughout this article for quick reference.

Clean our environment:

Build container image with Docker file:

```
docker build -f <Docker file name> -t <tag> .
```

Build container image from source (without Dockerfile):

```
mvn spring-boot:build-image
```

View layers of dependencies. Ensure the layering feature is enabled in spring-boot-maven-plugin before building the
application JAR:

```
java -Djarmode=layertools -jar application.jar list
```

Extract layers of dependencies. Ensure the layering feature is enabled in spring-boot-maven-plugin before building the
application JAR:

```
 java -Djarmode=layertools -jar application.jar extract
```

View list of container images

View layers inside container image (Ensure dive tool is installed):

```
dive <image ID or image tag>
```


## Github Actions 

  

## [](https://docs.github.com/en/actions/publishing-packages/publishing-java-packages-with-maven#publishing-packages-to-github-packages)Publishing packages to GitHub Packages

Each time you create a new release, you can trigger a workflow to publish your package. The workflow in the example below runs when the `release` event triggers with type `created`. The workflow publishes the package to GitHub Packages if CI tests pass. For more information on the `release` event, see "[Events that trigger workflows](https://docs.github.com/en/actions/reference/events-that-trigger-workflows#release)."

In this workflow, you can use the `setup-java` action. This action installs the given version of the JDK into the `PATH`, and also sets up a Maven _settings.xml_ for publishing the package to GitHub Packages. The generated _settings.xml_ defines authentication for a server with an `id` of `github`, using the `GITHUB_ACTOR` environment variable as the username and the `GITHUB_TOKEN` environment variable as the password. The `GITHUB_TOKEN` environment variable is assigned the value of the special `GITHUB_TOKEN` secret.

The `GITHUB_TOKEN` secret is set to an access token for the repository each time a job in a workflow begins. You should set the permissions for this access token in the workflow file to grant read access for the `contents` scope and write access for the `packages` scope. For more information, see "[Authenticating with the GITHUB\_TOKEN](https://docs.github.com/en/actions/configuring-and-managing-workflows/authenticating-with-the-github_token)."

For a Maven-based project, you can make use of these settings by creating a distribution repository in your _pom.xml_ file with an `id` of `github` that points to your GitHub Packages endpoint.

For example, if your organization is named "octocat" and your repository is named "hello-world", then the GitHub Packages configuration in _pom.xml_ would look similar to the below example.

With this configuration, you can create a workflow that publishes your package to GitHub Packages by making use of the automatically generated _settings.xml_.

This workflow performs the following steps:

1.  Checks out a copy of project's repository.
    
2.  Sets up the Java JDK, and also automatically configures the Maven _settings.xml_ file to add authentication for the `github` Maven repository to use the `GITHUB_TOKEN` environment variable.
    
3.  Runs the `mvn --batch-mode deploy` command to publish to GitHub Packages. The `GITHUB_TOKEN` environment variable will be set with the contents of the `GITHUB_TOKEN` secret. The `permissions` key specifies the access granted to the `GITHUB_TOKEN`.
    
    For more information about using secrets in your workflow, see "[Creating and using encrypted secrets](https://docs.github.com/en/actions/automating-your-workflow-with-github-actions/creating-and-using-encrypted-secrets)."


## Deploying Spring Boot Apps on custom servers

Spring Boot’s executable jars are ready-made for most popular cloud PaaS (Platform-as-a-Service) providers. These providers tend to require that you “bring your own container”. They manage application processes (not Java applications specifically), so they need an intermediary layer that adapts _your_ application to the _cloud’s_ notion of a running process.

Two popular cloud providers, Heroku and Cloud Foundry, employ a “buildpack” approach. The buildpack wraps your deployed code in whatever is needed to _start_ your application. It might be a JDK and a call to `java`, an embedded web server, or a full-fledged application server. A buildpack is pluggable, but ideally you should be able to get by with as few customizations to it as possible. This reduces the footprint of functionality that is not under your control. It minimizes divergence between development and production environments.

Ideally, your application, like a Spring Boot executable jar, has everything that it needs to run packaged within it.

In this section, we look at what it takes to get the [application that we developed](https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started.first-application) in the “Getting Started” section up and running in the Cloud.

### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.cloud-foundry)1.1. Cloud Foundry

Cloud Foundry provides default buildpacks that come into play if no other buildpack is specified. The Cloud Foundry [Java buildpack](https://github.com/cloudfoundry/java-buildpack) has excellent support for Spring applications, including Spring Boot. You can deploy stand-alone executable jar applications as well as traditional `.war` packaged applications.

Once you have built your application (by using, for example, `mvn clean package`) and have [installed the `cf` command line tool](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html), deploy your application by using the `cf push` command, substituting the path to your compiled `.jar`. Be sure to have [logged in with your `cf` command line client](https://docs.cloudfoundry.org/cf-cli/getting-started.html#login) before pushing an application. The following line shows using the `cf push` command to deploy an application:

```
$ cf push acloudyspringtime -p target/demo-0.0.1-SNAPSHOT.jar
```

In the preceding example, we substitute `acloudyspringtime` for whatever value you give `cf` as the name of your application.

See the [`cf push` documentation](https://docs.cloudfoundry.org/cf-cli/getting-started.html#push) for more options. If there is a Cloud Foundry [`manifest.yml`](https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html) file present in the same directory, it is considered.

At this point, `cf` starts uploading your application, producing output similar to the following example:

Uploading acloudyspringtime... **OK**
Preparing to start acloudyspringtime... **OK**
-----> Downloaded app package (**8.9M**)
-----> Java Buildpack Version: v3.12 (offline) | https://github.com/cloudfoundry/java-buildpack.git#6f25b7e
-----> Downloading Open Jdk JRE 1.8.0\_121 from https://java-buildpack.cloudfoundry.org/openjdk/trusty/x86\_64/openjdk-1.8.0\_121.tar.gz (found in cache)
       Expanding Open Jdk JRE to .java-buildpack/open\_jdk\_jre (1.6s)
-----> Downloading Open JDK Like Memory Calculator 2.0.2\_RELEASE from https://java-buildpack.cloudfoundry.org/memory-calculator/trusty/x86\_64/memory-calculator-2.0.2\_RELEASE.tar.gz (found in cache)
       Memory Settings: -Xss349K -Xmx681574K -XX:MaxMetaspaceSize=104857K -Xms681574K -XX:MetaspaceSize=104857K
-----> Downloading Container Certificate Trust Store 1.0.0\_RELEASE from https://java-buildpack.cloudfoundry.org/container-certificate-trust-store/container-certificate-trust-store-1.0.0\_RELEASE.jar (found in cache)
       Adding certificates to .java-buildpack/container\_certificate\_trust\_store/truststore.jks (0.6s)
-----> Downloading Spring Auto Reconfiguration 1.10.0\_RELEASE from https://java-buildpack.cloudfoundry.org/auto-reconfiguration/auto-reconfiguration-1.10.0\_RELEASE.jar (found in cache)
Checking status of app 'acloudyspringtime'...
  0 of 1 instances running (1 starting)
  ...
  0 of 1 instances running (1 starting)
  ...
  0 of 1 instances running (1 starting)
  ...
  1 of 1 instances running (1 running)

App started

Congratulations! The application is now live!

Once your application is live, you can verify the status of the deployed application by using the `cf apps` command, as shown in the following example:

```
$ cf apps
Getting applications in ...
OK

name                 requested state   instances   memory   disk   urls
...
acloudyspringtime    started           1/1         512M     1G     acloudyspringtime.cfapps.io
...
```

Once Cloud Foundry acknowledges that your application has been deployed, you should be able to find the application at the URI given. In the preceding example, you could find it at `https://acloudyspringtime.cfapps.io/`.

#### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.cloud-foundry.binding-to-services)1.1.1. Binding to Services

By default, metadata about the running application as well as service connection information is exposed to the application as environment variables (for example: `$VCAP_SERVICES`). This architecture decision is due to Cloud Foundry’s polyglot (any language and platform can be supported as a buildpack) nature. Process-scoped environment variables are language agnostic.

Environment variables do not always make for the easiest API, so Spring Boot automatically extracts them and flattens the data into properties that can be accessed through Spring’s `Environment` abstraction, as shown in the following example:

```
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MyBean implements EnvironmentAware {

    private String instanceId;

    @Override
    public void setEnvironment(Environment environment) {
        this.instanceId = environment.getProperty("vcap.application.instance_id");
    }

    

}
```

All Cloud Foundry properties are prefixed with `vcap`. You can use `vcap` properties to access application information (such as the public URL of the application) and service information (such as database credentials). See the [‘CloudFoundryVcapEnvironmentPostProcessor’](https://docs.spring.io/spring-boot/docs/2.6.2/api/org/springframework/boot/cloud/CloudFoundryVcapEnvironmentPostProcessor.html) Javadoc for complete details.

The [Java CFEnv](https://github.com/pivotal-cf/java-cfenv/) project is a better fit for tasks such as configuring a DataSource.

### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.kubernetes)1.2. Kubernetes

Spring Boot auto-detects Kubernetes deployment environments by checking the environment for `"*_SERVICE_HOST"` and `"*_SERVICE_PORT"` variables. You can override this detection with the `spring.main.cloud-platform` configuration property.

#### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.kubernetes.container-lifecycle)1.2.1. Kubernetes Container Lifecycle

When Kubernetes deletes an application instance, the shutdown process involves several subsystems concurrently: shutdown hooks, unregistering the service, removing the instance from the load-balancer… Because this shutdown processing happens in parallel (and due to the nature of distributed systems), there is a window during which traffic can be routed to a pod that has also begun its shutdown processing.

You can configure a sleep execution in a preStop handler to avoid requests being routed to a pod that has already begun shutting down. This sleep should be long enough for new requests to stop being routed to the pod and its duration will vary from deployment to deployment. The preStop handler can be configured by using the PodSpec in the pod’s configuration file as follows:

```
spec:
  containers:
  - name: "example-container"
    image: "example-image"
    lifecycle:
      preStop:
        exec:
          command: ["sh", "-c", "sleep 10"]
```

Once the pre-stop hook has completed, SIGTERM will be sent to the container and [graceful shutdown](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.graceful-shutdown) will begin, allowing any remaining in-flight requests to complete.

When Kubernetes sends a SIGTERM signal to the pod, it waits for a specified time called the termination grace period (the default for which is 30 seconds). If the containers are still running after the grace period, they are sent the SIGKILL signal and forcibly removed. If the pod takes longer than 30 seconds to shut down, which could be because you have increased `spring.lifecycle.timeout-per-shutdown-phase`, make sure to increase the termination grace period by setting the `terminationGracePeriodSeconds` option in the Pod YAML.

### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.heroku)1.3. Heroku

Heroku is another popular PaaS platform. To customize Heroku builds, you provide a `Procfile`, which provides the incantation required to deploy an application. Heroku assigns a `port` for the Java application to use and then ensures that routing to the external URI works.

You must configure your application to listen on the correct port. The following example shows the `Procfile` for our starter REST application:

web: java -Dserver.port=$PORT -jar target/demo-0.0.1-SNAPSHOT.jar

Spring Boot makes `-D` arguments available as properties accessible from a Spring `Environment` instance. The `server.port` configuration property is fed to the embedded Tomcat, Jetty, or Undertow instance, which then uses the port when it starts up. The `$PORT` environment variable is assigned to us by the Heroku PaaS.

This should be everything you need. The most common deployment workflow for Heroku deployments is to `git push` the code to production, as shown in the following example:

Which will result in the following:

Initializing repository, **done**.
Counting objects: 95, **done**.
Delta compression using up to 8 threads.
Compressing objects: 100% (78/78), **done**.
Writing objects: 100% (95/95), 8.66 MiB | 606.00 KiB/s, **done**.
Total 95 (delta 31), reused 0 (delta 0)

-----> Java app detected
-----> Installing OpenJDK 1.8... **done**
-----> Installing Maven 3.3.1... **done**
-----> Installing settings.xml... **done**
-----> Executing: mvn -B -DskipTests=true clean install

       \[INFO\] Scanning for projects...
       Downloading: https://repo.spring.io/...
       Downloaded: https://repo.spring.io/... (818 B at 1.8 KB/sec)
        ....
       Downloaded: https://s3pository.heroku.com/jvm/... (152 KB at 595.3 KB/sec)
       \[INFO\] Installing /tmp/build\_0c35a5d2-a067-4abc-a232-14b1fb7a8229/target/...
       \[INFO\] Installing /tmp/build\_0c35a5d2-a067-4abc-a232-14b1fb7a8229/pom.xml ...
       \[INFO\] ------------------------------------------------------------------------
       \[INFO\] **BUILD SUCCESS**
       \[INFO\] ------------------------------------------------------------------------
       \[INFO\] Total time: 59.358s
       \[INFO\] Finished at: Fri Mar 07 07:28:25 UTC 2014
       \[INFO\] Final Memory: 20M/493M
       \[INFO\] ------------------------------------------------------------------------

-----> Discovering process types
       Procfile declares types -> **web**

-----> Compressing... **done**, 70.4MB
-----> Launching... **done**, v6
       https://agile-sierra-1405.herokuapp.com/ **deployed to Heroku**

To git@heroku.com:agile-sierra-1405.git
 \* \[new branch\]      main -> main

### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.openshift)1.4. OpenShift

[OpenShift](https://www.openshift.com/) has many resources describing how to deploy Spring Boot applications, including:

### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.aws)1.5. Amazon Web Services (AWS)

Amazon Web Services offers multiple ways to install Spring Boot-based applications, either as traditional web applications (war) or as executable jar files with an embedded web server. The options include:

-   AWS Elastic Beanstalk
    
-   AWS Code Deploy
    
-   AWS OPS Works
    
-   AWS Cloud Formation
    
-   AWS Container Registry
    

Each has different features and pricing models. In this document, we describe to approach using AWS Elastic Beanstalk.

#### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.aws.beanstalk)1.5.1. AWS Elastic Beanstalk

As described in the official [Elastic Beanstalk Java guide](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/create_deploy_Java.html), there are two main options to deploy a Java application. You can either use the “Tomcat Platform” or the “Java SE platform”.

##### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.aws.beanstalk.tomcat-platform)Using the Tomcat Platform

This option applies to Spring Boot projects that produce a war file. No special configuration is required. You need only follow the official guide.

##### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.aws.beanstalk.java-se-platform)Using the Java SE Platform

This option applies to Spring Boot projects that produce a jar file and run an embedded web container. Elastic Beanstalk environments run an nginx instance on port 80 to proxy the actual application, running on port 5000. To configure it, add the following line to your `application.properties` file:

Upload binaries instead of sources

By default, Elastic Beanstalk uploads sources and compiles them in AWS. However, it is best to upload the binaries instead. To do so, add lines similar to the following to your `.elasticbeanstalk/config.yml` file:

```
deploy:
    artifact: target/demo-0.0.1-SNAPSHOT.jar
```

Reduce costs by setting the environment type

By default an Elastic Beanstalk environment is load balanced. The load balancer has a significant cost. To avoid that cost, set the environment type to “Single instance”, as described in [the Amazon documentation](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/environments-create-wizard.html#environments-create-wizard-capacity). You can also create single instance environments by using the CLI and the following command:

#### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.aws.summary)1.5.2. Summary

This is one of the easiest ways to get to AWS, but there are more things to cover, such as how to integrate Elastic Beanstalk into any CI / CD tool, use the Elastic Beanstalk Maven plugin instead of the CLI, and others. There is a [blog post](https://exampledriven.wordpress.com/2017/01/09/spring-boot-aws-elastic-beanstalk-example/) covering these topics more in detail.

### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.boxfuse)1.6. Boxfuse and Amazon Web Services

[Boxfuse](https://boxfuse.com/) works by turning your Spring Boot executable jar or war into a minimal VM image that can be deployed unchanged either on VirtualBox or on AWS. Boxfuse comes with deep integration for Spring Boot and uses the information from your Spring Boot configuration file to automatically configure ports and health check URLs. Boxfuse leverages this information both for the images it produces as well as for all the resources it provisions (instances, security groups, elastic load balancers, and so on).

Once you have created a [Boxfuse account](https://console.boxfuse.com/), connected it to your AWS account, installed the latest version of the Boxfuse Client, and ensured that the application has been built by Maven or Gradle (by using, for example, `mvn clean package`), you can deploy your Spring Boot application to AWS with a command similar to the following:

```
$ boxfuse run myapp-1.0.jar -env=prod
```

By default, Boxfuse activates a Spring profile named `boxfuse` on startup. If your executable jar or war contains an [`application-boxfuse.properties`](https://boxfuse.com/docs/payloads/springboot.html#configuration) file, Boxfuse bases its configuration on the properties it contains.

At this point, `boxfuse` creates an image for your application, uploads it, and configures and starts the necessary resources on AWS, resulting in output similar to the following example:

Fusing Image for myapp-1.0.jar ...
Image fused in 00:06.838s (53937 K) -> axelfontaine/myapp:1.0
Creating axelfontaine/myapp ...
Pushing axelfontaine/myapp:1.0 ...
Verifying axelfontaine/myapp:1.0 ...
Creating Elastic IP ...
Mapping myapp-axelfontaine.boxfuse.io to 52.28.233.167 ...
Waiting for AWS to create an AMI for axelfontaine/myapp:1.0 in eu-central-1 (this may take up to 50 seconds) ...
AMI created in 00:23.557s -> ami-d23f38cf
Creating security group boxfuse-sg\_axelfontaine/myapp:1.0 ...
Launching t2.micro instance of axelfontaine/myapp:1.0 (ami-d23f38cf) in eu-central-1 ...
Instance launched in 00:30.306s -> i-92ef9f53
Waiting for AWS to boot Instance i-92ef9f53 and Payload to start at https://52.28.235.61/ ...
Payload started in 00:29.266s -> https://52.28.235.61/
Remapping Elastic IP 52.28.233.167 to i-92ef9f53 ...
Waiting 15s for AWS to complete Elastic IP Zero Downtime transition ...
Deployment completed successfully. axelfontaine/myapp:1.0 is up and running at https://myapp-axelfontaine.boxfuse.io/

Your application should now be up and running on AWS.

### [](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.google)1.8. Google Cloud

Google Cloud has several options that can be used to launch Spring Boot applications. The easiest to get started with is probably App Engine, but you could also find ways to run Spring Boot in a container with Container Engine or on a virtual machine with Compute Engine.

To run in App Engine, you can create a project in the UI first, which sets up a unique identifier for you and also sets up HTTP routes. Add a Java app to the project and leave it empty and then use the [Google Cloud SDK](https://cloud.google.com/sdk/install) to push your Spring Boot app into that slot from the command line or CI build.

App Engine Standard requires you to use WAR packaging. Follow [these steps](https://github.com/GoogleCloudPlatform/java-docs-samples/tree/master/appengine-java8/springboot-helloworld/README.md) to deploy App Engine Standard application to Google Cloud.

Alternatively, App Engine Flex requires you to create an `app.yaml` file to describe the resources your app requires. Normally, you put this file in `src/main/appengine`, and it should resemble the following file:

```
service: "default"

runtime: "java"
env: "flex"

runtime_config:
  jdk: "openjdk8"

handlers:
- url: "/.*"
  script: "this field is required, but ignored"

manual_scaling:
  instances: 1

health_check:
  enable_health_check: false

env_variables:
  ENCRYPT_KEY: "your_encryption_key_here"
```

You can deploy the app (for example, with a Maven plugin) by adding the project ID to the build configuration, as shown in the following example:

```
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>appengine-maven-plugin</artifactId>
    <version>1.3.0</version>
    <configuration>
        <project>myproject</project>
    </configuration>
</plugin>
```

Then deploy with `mvn appengine:deploy` (if you need to authenticate first, the build fails).