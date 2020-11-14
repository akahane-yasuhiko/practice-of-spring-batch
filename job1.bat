pushd %~dp0
java -jar target\candystore-batch-0.0.1-SNAPSHOT.jar --spring.batch.job.names=job01 input.file=files/input/sales.csv error.file=files/output/error.csv opdate=2020-07-02
pause
