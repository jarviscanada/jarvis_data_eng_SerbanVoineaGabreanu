## Introduction

This project’s aim is to automate hardware info and usage gathering for Linux machines in clusters. It is designed to be usable by any Linux user, but it is targeted at administrators or DevOps engineers who need to see the status of all Linux machines. The project has been implemented on Rocky Linux 9, using Docker to run a PostgreSQL image where all of the host info and usage data is saved. It uses Bash shell scripts to get all the metrics data using Linux utilities, Docker to run a PostgreSQL instance, and crontab to schedule periodic data collection. The project is developed using the GitFlow branching model. Two main agent scripts are included: host_info.sh (one-time hardware specs) and host_usage.sh (periodic usage statistics). There is also a helper script, psql_docker.sh, which manages the database container, while ddl.sql and queries.sql define and query the monitoring schema.

## Quick Start

#Setup 
`./scripts/psql_docker.sh create postgres password `
`./scripts/psql_docker.sh start postgres password `

Create tables
`ddl.sql psql -h localhost -p 5432 -U postgres -d host_agent -f sql/ddl.sql`

Insert the hardware specification data into the DB using host_info.sh 
`./scripts/host_info.sh localhost 5432 host_agent postgres password `

Set crontab to run every minute 
`crontab -e * * * * * bash /absolute/path/to/scripts/host_usage.sh localhost 5432 host_agent postgres password > /tmp/host_usage.log 2>&1` 

To run after setup: 
Start the postgres services after a reboot
`sudo systemctl start docker `
`docker container start jrvs-psql `

Enter postgres mode 
`psql -h localhost -p 5432 -U postgres -d host_agent`


## Implementation

This project was implemented with an agent database architecture, where each linux host runs bash scripts that collect data metrics and send them to one PostgreSQL instance running through docker.

## Architecture

Each host runs:
-	host_info.sh once to register static hardware specs.
-	host_usage.sh every minute via cron to log usage metrics.
All of the data is written into the host_agent database inside the jrvs-psql Docker container.
queries.sql contains sample analytics queries that operations or data engineering teams can run to answer questions such as:
-	Which of the hosts are low on free memory?
-	Which of the hosts show high CPU utilization over a given time window?
- How many machines are currently part of the cluster?

## Scripts

- `psql_docker.sh`  
  Manages the PostgreSQL Docker container (create, start, stop).

- `host_info.sh`  
  Collects the static hardware specs for the current host and inserts them into `host_info`.

- `host_usage.sh`  
  Collects the CPU, memory, and disk usage metrics and inserts them into `host_usage` (needs to be configured to run every minute using crontab).

- `crontab`  
  Schedules `host_usage.sh` to run periodically (e.g., once per minute) for continuous monitoring.

- `sql/ddl.sql`  
  Contains the tables used to create the SQL tables for host_info and host_usage.


## Database Modeling

#### `host_info` table

| Column            | Type      | Description                                        |
|-------------------|-----------|----------------------------------------------------|
| id                | SERIAL PK | Unique identifier for each host                    |
| hostname          | VARCHAR   | Hostname of the machine                            |
| cpu_number        | INT       | Number of CPU cores                                |
| cpu_architecture  | VARCHAR   | CPU architecture (e.g., x86_64)                    |
| cpu_model         | VARCHAR   | CPU model name                                     |
| cpu_mhz           | REAL      | CPU clock speed in MHz                             |
| l2_cache          | INT       | Size of L2 cache (usually in KB)                  |
| total_mem         | INT       | Total memory available on the host (in MB)        |
| timestamp         | TIMESTAMP | Time when the hardware info was recorded (UTC)    |

#### `host_usage` table

| Column         | Type      | Description                                                   |
|----------------|-----------|---------------------------------------------------------------|
| timestamp      | TIMESTAMP | Time when the usage sample was taken (UTC)                   |
| host_id        | INT FK    | References `host_info.id` for the machine                    |
| memory_free    | INT       | Amount of free memory at the time of sampling (in MB)        |
| cpu_idle       | INT       | CPU idle percentage                                          |
| cpu_kernel     | INT       | CPU time spent in kernel mode (percentage)                   |
| disk_io        | INT       | Disk I/O metric (e.g., blocks read/written from `vmstat`)    |
| disk_available | INT       | Available disk space on the main filesystem (in MB)          |


## Test

- Ran `psql_docker.sh` (`create`, `start`, `stop`) and verified the container state with `docker ps`.
- Executed `sql/ddl.sql` and confirmed `host_info` and `host_usage` were created using `\dt` in `psql`.
- Ran `host_info.sh` once and checked that a correct row appeared in `host_info`.
- Ran `host_usage.sh` manually and via `crontab` (every minute), then verified the new rows in `host_usage` with proper values.

## Deployment

Version control:
-	The project development is tracked using Git and hosted on GitHub under the develop branch using a GitFlow workflow.
Database runtime:
-	PostgreSQL runs inside a Docker container managed by psql_docker.sh.
Agent deployment:
-	Bash scripts (host_info.sh and host_usage.sh) are copied to each Linux host under a scripts/ directory.
-	host_info.sh is executed once per host during onboarding.
-	host_usage.sh is scheduled with crontab to run every minute.
Operations:
-	On reboot or maintenance, admins start Docker and the jrvs-psql container, and the cron job goes back to inserting metrics automatically.

## Improvements
1.	The current host info metrics are only recorded once; if hardware is changed, this information won’t be updated. An improvement would be to handle hardware changes so that updates to CPU, memory, disk config, etc., are captured through host_info without manual intervention.
2.	Another improvement would be the implementation of automated alerts for issues such as low disk space and consistently high CPU usage, along with automated, easy-to-access summary reports.
3.	Adding input validation to the Bash scripts to verify that required commands (such as vmstat) are working. If there are any issues, there should be a clear error message with effective error handling


