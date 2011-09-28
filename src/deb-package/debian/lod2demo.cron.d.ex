#
# Regular cron jobs for the lod2demo package
#
0 4	* * *	root	[ -x /usr/bin/lod2demo_maintenance ] && /usr/bin/lod2demo_maintenance
