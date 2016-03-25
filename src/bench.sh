javac -cp jmh-core-1.11.3.jar:jopt-simple-4.6.jar:jmh-generator-annprocess-1.11.3.jar:. Main.java 
javac -cp jmh-core-1.11.3.jar:jopt-simple-4.6.jar:jmh-generator-annprocess-1.11.3.jar:. ./org/test/JmhPerfTest.java 

java -Djmh.shutdownTimeout=1 -cp commons-math3-3.2.jar:jmh-core-1.11.3.jar:jopt-simple-4.6.jar:jmh-generator-annprocess-1.11.3.jar:mariadb-java-client-1.3.5.jar:. Main mariadb_1.3.5.csv 
java -Djmh.shutdownTimeout=1 -cp commons-math3-3.2.jar:jmh-core-1.11.3.jar:jopt-simple-4.6.jar:jmh-generator-annprocess-1.11.3.jar:mariadb-java-client-1.4.0-SNAPSHOT.jar:. Main mariadb_1.4.0.csv 
java -Djmh.shutdownTimeout=1 -cp commons-math3-3.2.jar:jmh-core-1.11.3.jar:jopt-simple-4.6.jar:jmh-generator-annprocess-1.11.3.jar:mysql-connector-java-5.1.38.jar:. Main mysql_5.1.38.csv 

