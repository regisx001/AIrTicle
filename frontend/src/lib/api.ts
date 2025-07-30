import type {
	Article,
	CreateArticleRequest,
	AnalyseResult,
	AnalyseHistory,
	ArticlesResponse
} from '$lib/types';

const API_BASE_URL = 'http://localhost:8080/api';

class ApiClient {
	private async request<T>(
		endpoint: string,
		options: RequestInit = {}
	): Promise<{ success: boolean; data?: T; error?: string }> {
		try {
			const response = await fetch(`${API_BASE_URL}${endpoint}`, {
				headers: {
					'Content-Type': 'application/json',
					...options.headers
				},
				...options
			});

			if (!response.ok) {
				const errorText = await response.text();
				return {
					success: false,
					error: `HTTP ${response.status}: ${errorText || response.statusText}`
				};
			}

			const data = await response.json();
			return { success: true, data };
		} catch (error) {
			console.error(`API request failed for ${endpoint}:`, error);
			return {
				success: false,
				error: error instanceof Error ? error.message : 'Network error'
			};
		}
	}

	// Article endpoints
	async getArticles(
		page = 0,
		size = 10,
		sort = 'createdAt,desc'
	): Promise<{ success: boolean; data?: ArticlesResponse; error?: string }> {
		return this.request<ArticlesResponse>(`/articles?page=${page}&size=${size}&sort=${sort}`);
	}

	async getArticle(id: string): Promise<{ success: boolean; data?: Article; error?: string }> {
		return this.request<Article>(`/articles/${id}`);
	}

	async createArticle(
		article: CreateArticleRequest
	): Promise<{ success: boolean; data?: Article; error?: string }> {
		return this.request<Article>('/articles', {
			method: 'POST',
			body: JSON.stringify(article)
		});
	}

	// Analysis endpoints
	async getAnalysisResult(
		articleId: string
	): Promise<{ success: boolean; data?: AnalyseResult; error?: string }> {
		return this.request<AnalyseResult>(`/articles/${articleId}/review`);
	}

	async triggerManualAnalysis(
		articleId: string
	): Promise<{ success: boolean; data?: AnalyseResult; error?: string }> {
		return this.request<AnalyseResult>(`/articles/${articleId}/review`, {
			method: 'POST'
		});
	}

	async getAnalysisHistory(
		articleId: string
	): Promise<{ success: boolean; data?: AnalyseHistory[]; error?: string }> {
		return this.request<AnalyseHistory[]>(`/articles/${articleId}/history`);
	}
}

export const apiClient = new ApiClient();
