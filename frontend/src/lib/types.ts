// Types for the AIrTicle application based on the API documentation

export type ArticleStatus =
	| 'DRAFT'
	| 'SUBMITTED_FOR_APPROVAL'
	| 'UNDER_AI_REVIEW'
	| 'AI_APPROVED'
	| 'AI_REJECTED'
	| 'MANUAL_REVIEW_REQUIRED'
	| 'APPROVED'
	| 'REJECTED'
	| 'PUBLISHED'
	| 'ARCHIVED';

export type AnalysisDecision = 'APPROVED' | 'REJECTED' | 'MANUAL_REVIEW_REQUIRED';

export interface Article {
	id: string;
	title: string;
	content: string;
	featuredImage?: string;
	status: ArticleStatus;
	isPublished: boolean;
	publishedAt?: string;
	feedback?: string;
	createdAt: string;
	updatedAt: string;
	rejectedAt?: string;
	rejectedBy?: string;
	approvedAt?: string;
	approvedBy?: string;
}

export interface CreateArticleRequest {
	title: string;
	content: string;
	featuredImage?: string;
}

export interface AnalyseResult {
	id: string;
	articleId: string;
	decision: AnalysisDecision;
	confidenceScore: number;
	aiAnalysis: string;
	recommendations: string;
	readabilityScore: number;
	grammarScore: number;
	seoScore: number;
	originalityScore: number;
	analyzedAt: string;
	aiModel: string;
	processingTimeMs: number;
}

export interface AnalyseHistory {
	id: string;
	articleId: string;
	fromStatus?: ArticleStatus;
	toStatus: ArticleStatus;
	performedBy: string;
	reason: string;
	notes?: string;
	metadata?: string;
	confidenceScore?: number;
	aiModel?: string;
	processingTimeMs?: number;
	createdAt: string;
	updatedAt: string;
}

export interface PaginatedResponse<T> {
	content: T[];
	pageable: {
		pageNumber: number;
		pageSize: number;
	};
	totalElements: number;
	totalPages: number;
}

export type ArticlesResponse = PaginatedResponse<Article>;

// Form states using Svelte 5 runes
export interface ArticleFormState {
	title: string;
	content: string;
	featuredImage: string;
	isSubmitting: boolean;
	errors: {
		title?: string;
		content?: string;
		featuredImage?: string;
		general?: string;
	};
}

// API Response types
export interface ApiResponse<T> {
	success: boolean;
	data?: T;
	error?: string;
	message?: string;
}
