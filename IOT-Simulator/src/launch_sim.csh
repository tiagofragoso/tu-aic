foreach i (`seq 1 60`) # the script works for 10 minutes
	python3 sim.py ./iwildcam_synthesized_idaho/;
	sleep 10;
end