FROM openjdk:17-alpine

RUN mkdir /app
WORKDIR /app

COPY target/arcgis-latest.jar /app/app.jar
CMD ["java", "-Dspring.profiles.active=dev", "-jar" ,"/app/app.jar"]
#docker login springbatchcloudregistry.azurecr.io --username SpringBatchCloudRegistry --password npYGi9emrNy476GP5yFiJCSftSrSAItvob1sNIf7A3+ACRDjgmi8
# docker tag arcgis-app:latest springbatchcloudregistry.azurecr.io/arcgis-app:latest
# docker push springbatchcloudregistry.azurecr.io/arcgis-app:latest