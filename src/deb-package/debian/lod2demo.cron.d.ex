#
# Regular cron jobs for the testarch package
#
0 4	* * *	root	[ -x /usr/bin/testarch_maintenance ] && /usr/bin/testarch_maintenance
