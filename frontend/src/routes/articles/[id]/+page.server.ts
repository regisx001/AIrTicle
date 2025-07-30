import { invalidateAll } from '$app/navigation';
import type { PageServerLoad, Actions } from './$types';
import { error, redirect } from '@sveltejs/kit';

export const load: PageServerLoad = async ({ params, fetch }) => {
	const { id } = params;

	try {
		// Load article details
		const articleResponse = await fetch(`http://localhost:8080/api/articles/${id}`);

		if (!articleResponse.ok) {
			if (articleResponse.status === 404) {
				throw error(404, 'Article not found');
			}
			throw error(articleResponse.status, `Failed to fetch article: ${articleResponse.statusText}`);
		}

		const article = await articleResponse.json();

		// Try to load analysis results (may not exist for new articles)
		let analysisResult = null;
		try {
			const analysisResponse = await fetch(`http://localhost:8080/api/articles/${id}/review`);
			if (analysisResponse.ok) {
				analysisResult = await analysisResponse.json();
			}
		} catch (e) {
			// Analysis may not exist yet, that's ok
			console.log('No analysis results yet for article:', id);
		}

		// Try to load analysis history
		let analysisHistory = [];
		try {
			const historyResponse = await fetch(`http://localhost:8080/api/articles/${id}/history`);
			if (historyResponse.ok) {
				analysisHistory = await historyResponse.json();
			}
		} catch (e) {
			// History may not exist yet, that's ok
			console.log('No analysis history yet for article:', id);
		}

		return {
			article,
			analysisResult,
			analysisHistory
		};
	} catch (e) {
		if (e instanceof Response) {
			throw e; // Re-throw SvelteKit errors
		}
		console.error('Error loading article:', e);
		throw error(500, 'Failed to load article');
	}
};

export const actions = {
	triggerAnalysis: async ({ params, fetch }) => {
		const { id } = params;

		try {
			const response = await fetch(`http://localhost:8080/api/articles/${id}/review`, {
				method: 'POST'
			});

			if (!response.ok) {
				return {
					success: false,
					error: `Failed to trigger analysis: ${response.statusText}`
				};
			}

			// Redirect back to the article page to see the new results
		} catch (error) {
			if (error instanceof Response) {
				throw error; // Re-throw redirects
			}

			console.error('Error triggering analysis:', error);
			return {
				success: false,
				error: 'An unexpected error occurred while triggering analysis'
			};
		}

		redirect(308, `/articles/${id}`);
	}
} satisfies Actions;
