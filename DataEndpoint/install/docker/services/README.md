If you encounter the elasticsearch error:

ERROR: bootstrap checks failed | max > virtual memory areas vm.max_map_count 65530 is too low, increase to > at least 262144

Open cmd and type:
wsl -d docker-desktop
sysctl -w vm.max_map_count=262144