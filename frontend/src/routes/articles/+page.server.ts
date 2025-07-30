import type { PageServerLoad } from './$types';
import { error } from '@sveltejs/kit';

export const load: PageServerLoad = async ({ url, fetch }) => {
	const page = parseInt(url.searchParams.get('page') || '0');
	const size = parseInt(url.searchParams.get('size') || '10');
	const sort = url.searchParams.get('sort') || 'createdAt,desc';

	try {
		const response = await fetch(
			`http://localhost:8080/api/articles?page=${page}&size=${size}&sort=${sort}`
		);

		if (!response.ok) {
			throw error(response.status, `Failed to fetch articles: ${response.statusText}`);
		}

		const articlesData = await response.json();

		return {
			articles: articlesData,
			currentPage: page,
			pageSize: size,
			sort
		};
	} catch (e) {
		console.error('Error loading articles:', e);
		throw error(500, 'Failed to load articles');
	}
};
