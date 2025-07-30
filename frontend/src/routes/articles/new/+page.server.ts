import { redirect, fail } from '@sveltejs/kit';
import type { Actions } from './$types';

export const actions = {
	create: async ({ request, fetch }) => {
		const data = await request.formData();
		const title = data.get('title') as string;
		const content = data.get('content') as string;
		const featuredImage = data.get('featuredImage') as string;

		// Validation
		if (!title?.trim()) {
			return fail(400, {
				error: 'Title is required',
				title,
				content,
				featuredImage
			});
		}

		if (!content?.trim()) {
			return fail(400, {
				error: 'Content is required',
				title,
				content,
				featuredImage
			});
		}

		if (content.trim().split(/\s+/).length < 50) {
			return fail(400, {
				error: 'Content must be at least 50 words long',
				title,
				content,
				featuredImage
			});
		}

		try {
			const articleData = {
				title: title.trim(),
				content: content.trim(),
				...(featuredImage?.trim() && { featuredImage: featuredImage.trim() })
			};

			const response = await fetch('http://localhost:8080/api/articles', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(articleData)
			});

			if (!response.ok) {
				const errorText = await response.text();
				return fail(response.status, {
					error: `Failed to create article: ${errorText}`,
					title,
					content,
					featuredImage
				});
			}

			const article = await response.json();

			// Redirect to the article detail page
			throw redirect(303, `/articles/${article.id}`);
		} catch (error) {
			if (error instanceof Response) {
				throw error; // Re-throw redirects
			}

			console.error('Error creating article:', error);
			return fail(500, {
				error: 'An unexpected error occurred while creating the article',
				title,
				content,
				featuredImage
			});
		}
	}
} satisfies Actions;
