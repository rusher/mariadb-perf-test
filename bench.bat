"C:\Program Files\Java\jdk1.8.0_60\bin\javac.exe" -cp lib/jmh-core-1.11.3.jar;lib/jopt-simple-4.6.jar;lib/jmh-generator-annprocess-1.11.3.jar;. ./org/test/Main.java
"C:\Program Files\Java\jdk1.8.0_60\bin\javac.exe" -cp ./lib/jmh-core-1.11.3.jar;./lib/jopt-simple-4.6.jar;./lib/jmh-generator-annprocess-1.11.3.jar;. ./org/test/JmhPerfTest.java

java  -Djmh.shutdownTimeout=1 -cp ./lib/commons-math3-3.2.jar;./lib/jmh-core-1.11.3.jar;./lib/jopt-simple-4.6.jar;./lib/jmh-generator-annprocess-1.11.3.jar;./lib/mariadb-java-client-1.4.2.jar;. org.test.Main "mariadb_1.4.2.csv"
java  -Djmh.shutdownTimeout=1 -cp ./lib/commons-math3-3.2.jar;./lib/jmh-core-1.11.3.jar;./lib/jopt-simple-4.6.jar;./lib/jmh-generator-annprocess-1.11.3.jar;./lib/mysql-connector-java-5.1.38.jar;. org.test.Main "mysql_5.1.38.csv"

