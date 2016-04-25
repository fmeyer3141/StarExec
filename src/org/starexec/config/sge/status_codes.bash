# STAREXEC JOB STATUS CODES
# These variables are defined for use in the jobscript
# They MUST correspond with the status codes defined in the starexec schema

STATUS_UNKNOWN=0
STATUS_PENDING_SUBMIT=1
STATUS_ENQUEUED=2
STATUS_RUNNING=4
STATUS_COMPLETE=7
ERROR_SGE_REJECT=8
ERROR_SUBMIT_FAIL=9
ERROR_RESULTS=10
ERROR_RUNSCRIPT=11
ERROR_BENCHMARK=12
ERROR_DISK_QUOTA_EXCEEDED=13
EXCEED_RUNTIME=14
EXCEED_CPU=15
EXCEED_FILE_WRITE=16
EXCEED_MEM=17
ERROR_GENERAL=18
STATUS_PROCESSING_RESULTS=19
STATUS_PAUSED=20
STATUS_KILLED=21
STATUS_NOT_REACHED=23
