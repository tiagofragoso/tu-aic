export const formatDate = (d: number) => {
	const date = new Date(d);
	return date.toLocaleString();
};