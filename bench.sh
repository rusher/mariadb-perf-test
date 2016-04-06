javac -cp ./lib/jmh-core-1.11.3.jar:./lib/jopt-simple-4.6.jar:./lib/jmh-generator-annprocess-1.11.3.jar:. ./org/test/Main.java
javac -cp ./lib/jmh-core-1.11.3.jar:./lib/jopt-simple-4.6.jar:./lib/jmh-generator-annprocess-1.11.3.jar:. ./org/test/JmhPerfTest.java

java -Djmh.shutdownTimeout=1 -cp ./lib/commons-math3-3.2.jar:./lib/jmh-core-1.11.3.jar:./lib/jopt-simple-4.6.jar:./lib/jmh-generator-annprocess-1.11.3.jar:./lib/mariadb-java-client-1.4.1-SNAPSHOT.jar:. org.test.Main mariadb_1.4.1.csv
java -Djmh.shutdownTimeout=1 -cp ./lib/commons-math3-3.2.jar:./lib/jmh-core-1.11.3.jar:./lib/jopt-simple-4.6.jar:./lib/jmh-generator-annprocess-1.11.3.jar:./lib/mysql-connector-java-5.1.38.jar:. org.test.Main mysql_5.1.38.csv
java -Djmh.shutdownTimeout=1 -cp ./lib/commons-math3-3.2.jar:./lib/jmh-core-1.11.3.jar:./lib/jopt-simple-4.6.jar:./lib/jmh-generator-annprocess-1.11.3.jar:./lib/mariadb-java-client-1.1.5.jar:. org.test.Main mariadb_1.1.5.csv
java -Djmh.shutdownTimeout=1 -cp ./lib/commons-math3-3.2.jar:./lib/jmh-core-1.11.3.jar:./lib/jopt-simple-4.6.jar:./lib/jmh-generator-annprocess-1.11.3.jar:./lib/mariadb-java-client-1.1.8.jar:. org.test.Main mariadb_1.1.8.csv
java -Djmh.shutdownTimeout=1 -cp ./lib/commons-math3-3.2.jar:./lib/jmh-core-1.11.3.jar:./lib/jopt-simple-4.6.jar:./lib/jmh-generator-annprocess-1.11.3.jar:./lib/mariadb-java-client-1.3.5.jar:. org.test.Main mariadb_1.3.5.csv

