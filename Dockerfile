FROM openjdk:7
RUN git clone https://github.com/shruthi-venkatesan/packageIndexer.git /pkgIndexer/
WORKDIR /pkgIndexer/src/
RUN javac packageIndexer/*.java
EXPOSE 8080
CMD ["java", "packageIndexer.PkgIndexerServer"]
