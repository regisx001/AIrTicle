<script lang="ts">
	import { onMount } from 'svelte';
	import { Button } from '$lib/components/ui/button';
	import { StatusBadge } from '$lib/components/ui/status-badge';
	import type { Article } from '$lib/types';
	import { apiClient } from '$lib/api';
	import { formatDate, truncateText, wordCount, estimateReadingTime } from '$lib/utils/format';
	import PlusIcon from '@lucide/svelte/icons/plus';
	import TrendingUpIcon from '@lucide/svelte/icons/trending-up';
	import FileTextIcon from '@lucide/svelte/icons/file-text';
	import ClockIcon from '@lucide/svelte/icons/clock';
	import CheckCircleIcon from '@lucide/svelte/icons/check-circle';

	// State using Svelte 5 runes
	let articles = $state<Article[]>([]);
	let loading = $state(true);
	let error = $state<string | null>(null);

	// Stats derived from articles
	const stats = $derived.by(() => {
		const total = articles.length;
		const approved = articles.filter((a) =>
			['APPROVED', 'AI_APPROVED', 'PUBLISHED'].includes(a.status)
		).length;
		const underReview = articles.filter((a) =>
			['UNDER_AI_REVIEW', 'SUBMITTED_FOR_APPROVAL', 'MANUAL_REVIEW_REQUIRED'].includes(a.status)
		).length;
		const published = articles.filter((a) => a.status === 'PUBLISHED').length;

		return {
			total,
			approved,
			underReview,
			published
		};
	});

	onMount(async () => {
		await loadArticles();
	});

	async function loadArticles() {
		loading = true;
		error = null;

		const response = await apiClient.getArticles(0, 5, 'createdAt,desc');

		if (response.success && response.data) {
			articles = response.data.content;
		} else {
			error = response.error || 'Failed to load articles';
		}

		loading = false;
	}
</script>

<svelte:head>
	<title>AIrTicle - AI-Powered Article Validation System</title>
	<meta
		name="description"
		content="Intelligent article validation and approval system with AI-powered analysis"
	/>
</svelte:head>

<div class="space-y-8">
	<!-- Hero Section -->
	<div class="space-y-4 text-center">
		<h1 class="text-4xl font-bold tracking-tight sm:text-6xl">
			Welcome to <span class="text-primary">AIrTicle</span>
		</h1>
		<p class="text-muted-foreground mx-auto max-w-3xl text-xl">
			AI-powered article validation and approval system. Automatically analyze content quality,
			grammar, SEO optimization, and originality with intelligent recommendations.
		</p>
		<div class="flex justify-center gap-4 pt-4">
			<Button href="/articles/new" size="lg">
				<PlusIcon class="mr-2 h-5 w-5" />
				Create New Article
			</Button>
			<Button href="/articles" variant="outline" size="lg">
				<FileTextIcon class="mr-2 h-5 w-5" />
				View All Articles
			</Button>
		</div>
	</div>

	<!-- Stats Grid -->
	<div class="grid grid-cols-1 gap-6 md:grid-cols-4">
		<div class="bg-card rounded-lg border p-6">
			<div class="flex items-center space-x-2">
				<FileTextIcon class="h-5 w-5 text-blue-500" />
				<h3 class="font-semibold">Total Articles</h3>
			</div>
			<p class="mt-2 text-3xl font-bold">{stats.total}</p>
			<p class="text-muted-foreground text-sm">All time</p>
		</div>

		<div class="bg-card rounded-lg border p-6">
			<div class="flex items-center space-x-2">
				<CheckCircleIcon class="h-5 w-5 text-green-500" />
				<h3 class="font-semibold">Approved</h3>
			</div>
			<p class="mt-2 text-3xl font-bold">{stats.approved}</p>
			<p class="text-muted-foreground text-sm">Ready to publish</p>
		</div>

		<div class="bg-card rounded-lg border p-6">
			<div class="flex items-center space-x-2">
				<ClockIcon class="h-5 w-5 text-yellow-500" />
				<h3 class="font-semibold">Under Review</h3>
			</div>
			<p class="mt-2 text-3xl font-bold">{stats.underReview}</p>
			<p class="text-muted-foreground text-sm">Being analyzed</p>
		</div>

		<div class="bg-card rounded-lg border p-6">
			<div class="flex items-center space-x-2">
				<TrendingUpIcon class="h-5 w-5 text-emerald-500" />
				<h3 class="font-semibold">Published</h3>
			</div>
			<p class="mt-2 text-3xl font-bold">{stats.published}</p>
			<p class="text-muted-foreground text-sm">Live articles</p>
		</div>
	</div>

	<!-- Recent Articles -->
	<div class="space-y-6">
		<div class="flex items-center justify-between">
			<h2 class="text-2xl font-bold">Recent Articles</h2>
			<Button href="/articles" variant="outline">View All</Button>
		</div>

		{#if loading}
			<div class="py-8 text-center">
				<div class="border-primary mx-auto h-8 w-8 animate-spin rounded-full border-b-2"></div>
				<p class="text-muted-foreground mt-2">Loading articles...</p>
			</div>
		{:else if error}
			<div class="space-y-4 py-8 text-center">
				<p class="text-destructive">⚠️ {error}</p>
				<Button onclick={loadArticles} variant="outline">Try Again</Button>
			</div>
		{:else if articles.length === 0}
			<div class="space-y-4 py-12 text-center">
				<FileTextIcon class="text-muted-foreground mx-auto h-16 w-16" />
				<h3 class="text-xl font-semibold">No articles yet</h3>
				<p class="text-muted-foreground">
					Create your first article to get started with AI-powered validation.
				</p>
				<Button href="/articles/new">
					<PlusIcon class="mr-2 h-4 w-4" />
					Create First Article
				</Button>
			</div>
		{:else}
			<div class="grid gap-6">
				{#each articles as article (article.id)}
					<div class="bg-card rounded-lg border p-6 transition-shadow hover:shadow-md">
						<div class="flex items-start justify-between">
							<div class="flex-1 space-y-3">
								<div class="flex items-center gap-3">
									<h3 class="text-lg font-semibold">
										<a href="/articles/{article.id}" class="hover:text-primary transition-colors">
											{article.title}
										</a>
									</h3>
									<StatusBadge status={article.status} />
								</div>

								<p class="text-muted-foreground">
									{truncateText(article.content, 200)}
								</p>

								<div class="text-muted-foreground flex items-center gap-4 text-sm">
									<span>{formatDate(article.createdAt)}</span>
									<span>•</span>
									<span>{wordCount(article.content)} words</span>
									<span>•</span>
									<span>{estimateReadingTime(article.content)}</span>
								</div>
							</div>

							{#if article.featuredImage}
								<img
									src={article.featuredImage}
									alt={article.title}
									class="ml-4 h-24 w-24 rounded-md object-cover"
								/>
							{/if}
						</div>
					</div>
				{/each}
			</div>
		{/if}
	</div>
</div>
