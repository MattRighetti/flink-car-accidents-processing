# Car accidents data processing in Apache Flink

<p align="center">
    <img src="flink.png" height="200px">
    <img src="nypd.png" height="200px">
</p>

## Team Members
- [Mattia Righetti](https://github.com/MattRighetti)
- [Nicolò Felicioni](https://github.com/ciamir51)
- [Luca Conterio](https://github.com/luca-conterio)

## Assignment
The goal of this project is to infer qualitative data regarding the car accidents in New York City.  
  
Extract the following information:
|Query|Description|
|:-|:-|
|Q1| <ul><li>Number of lethal accidents per week throughout the entire dataset</li></ul> |
|Q2| <ul><li>Number of accidents and percentage of number of deaths per contributing factor in the dataset.</li><ul><li>I.e., for each contributing factor, we want to know how many accidents were due to that contributing factor and what percentage of these accidents were also lethal.</li></ul></ul> |
|Q3| <ul><li>Number of accidents and average number of lethal accidents per week per borough.</li><ul><li>I.e., for each borough, we want to know how many accidents there were in that borough each week as well as the average number of lethal accidents that the borough had per week.</li></ul></ul> |

## CSV structure

|Field Name|Type|
|:-|:-:|
|`DATE`|`String`|
|`TIME`|`String`|
|`BOROUGH`|`String`|
|`ZIP CODE`|`Integer`|
|`LATITUDE`|`Float`|
|`LONGITUDE`|`Float`|
|`LOCATION`|`String`|
|`ON STREET NAME`|`String`|
|`CROSS STREET NAME`|`String`|
|`OFF STREET NAME`|`String`|
|`NUMBER OF PERSONS INJURED`|`Integer`|
|`NUMBER OF PERSONS KILLED`|`Integer`|
|`NUMBER OF PEDESTRIANS INJURED`|`Integer`|
|`NUMBER OF PEDESTRIANS KILLED`|`Integer`|
|`NUMBER OF CYCLIST INJURED`|`Integer`|
|`NUMBER OF CYCLIST KILLED`|`Integer`|
|`NUMBER OF MOTORIST INJURED`|`Integer`|
|`NUMBER OF MOTORIST KILLED`|`Integer`|
|`CONTRIBUTING FACTOR VEHICLE 1`|`String`|
|`CONTRIBUTING FACTOR VEHICLE 2`|`String`|
|`CONTRIBUTING FACTOR VEHICLE 3`|`String`|
|`CONTRIBUTING FACTOR VEHICLE 4`|`String`|
|`CONTRIBUTING FACTOR VEHICLE 5`|`String`|
|`UNIQUE KEY`|`Long`|
|`VEHICLE TYPE CODE 1`|`String`|
|`VEHICLE TYPE CODE 2`|`String`|
|`VEHICLE TYPE CODE 3`|`String`|
|`VEHICLE TYPE CODE 4`|`String`|
|`VEHICLE TYPE CODE 5`|`String`|

## How to compile this program
By executing these commands a **target** folder will be generated in the project's directory that's going to contained the generated **.jar** executables
1. `mvn clean install`
2. `mvn package`
  
## How to run this program
1. Fire up Flink using the provided `docker-compose.yaml`. You can set in this file the number of TaskManagers you want by modifying the `scale` parameter.
2. Copy the input file to the JobManager and to each TaskManager: `docker cp <path_to_input_file> <jobmanager_docker_instance_name>:<destination_path>`, e.g. `docker cp files/car-accidents/NYPD_Motor_Vehicle_Collisions.csv  flink-car-accidents-processing_jobmanager_1:/opt/flink`
3. Submit the job to flink with `flink run <jar> --nypd_data_file <path_to_NYPD_csv> --query <query-number-to-run> --output <path_to_output_file>` e.g. `--nypd_data_file /opt/flink/NYPD_Motor_Vehicle_Collisions.csv --query 1 --output /opt/flink/output1`
