#
# Regular cron jobs for the lod2statworkbench package
#
0 4	* * *	root	[ -x /usr/bin/lod2statworkbench_maintenance ] && /usr/bin/lod2statworkbench_maintenance
