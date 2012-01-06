#
# Regular cron jobs for the valiant package
#
0 4	* * *	root	[ -x /usr/bin/valiant_maintenance ] && /usr/bin/valiant_maintenance
