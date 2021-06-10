# Scheduling Algorithms

This project involves implementing several different process scheduling algorithms. The scheduler will be assigned a predefined set of tasks and will schedule the tasks based on the selected scheduling algorithm. Each task has an arrival time and is assigned a priority and CPU burst. The following scheduling algorithms will be implemented:

- First-come, first-served (fcfs), which schedules tasks in the order in which they request the CPU .

- Shortest-job-first (sjf), which schedules tasks in order of the length of the tasks’ next CPU burst.

- Priority scheduling (pri), which schedules tasks based on priority.

- Round-robin (rr) scheduling, where each task is run for a time quantum (or for the remainder of its CPU burst).

- Priority with round-robin (pri-rr), which schedules tasks in order of priority and uses round-robin scheduling for tasks with equal priority.

Priorities range from 1 to 10, where a higher numeric value indicates a higher relative priority. For round-robin scheduling, the length of a time quantum is 10 milliseconds.

## Implementation:

- The implementation of this project may be completed in any programming language (although Python or Java recommended)

- The tasks are kept on a file and has the form [task name] [arrival time] [priority] [ CPU burst], with the following example format:

    T1, 1, 4, 20

...T2, 4, 2, 25

...T3, 6, 3, 25

...T4, 2, 3, 15

...T5, 5, 10, 10

- The program should read the name of the scheduler algorithm and the task file as command line input and produce a text file named “output.txt” with the following example output:

...>> java Schedule fcfs schedule.txt

...>> cat output.txt

- First Come First Serve Scheduling

...Will run Name: T1

...Priority: 4

...Burst: 20


...Task T1 finished


...Will run Name: T4

...Priority: 10

...Burst: 15


...Task T4 Finished


...Will run Name: T2

...Priority: 2

...Burst: 25


...Task T2 Finished 



...Will run Name: T5

...Priority: 10

...Burst: 10


...Task T5 Finished


...Will run Name: T3

...Priority: 3

...Burst: 25


...Task T3 Finished


- Calculate and print the average turnaround time and waiting time or each of the scheduling algorithms.
