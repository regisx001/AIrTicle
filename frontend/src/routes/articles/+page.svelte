<script lang="ts">
	import { goto } from '$app/navigation';
	import { page } from '$app/stores';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { StatusBadge } from '$lib/components/ui/status-badge';
	import type { Article, ArticlesResponse } from '$lib/types';
	import { formatDate, truncateText, wordCount, estimateReadingTime } from '$lib/utils/format';
	import SearchIcon from '@lucide/svelte/icons/search';
	import PlusIcon from '@lucide/svelte/icons/plus';
	import FilterIcon from '@lucide/svelte/icons/filter';
	import ChevronLeftIcon from '@lucide/svelte/icons/chevron-left';
	import ChevronRightIcon from '@lucide/svelte/icons/chevron-right';

	let { data } = $props();

	const articles: ArticlesResponse = data.articles;
	const currentPage: number = data.currentPage;
	const pageSize: number = data.pageSize;
	const sort: string = data.sort;

	// Search and filter state
	let searchQuery = $state('');
	let statusFilter = $state('all');

	// Filtered articles based on search and status
	const filteredArticles = $derived(() => {
		let filtered = articles.content;

		if (searchQuery.trim()) {
			const query = searchQuery.toLowerCase();
			filtered = filtered.filter(
				(article) =>
					article.title.toLowerCase().includes(query) ||
					article.content.toLowerCase().includes(query)
			);
		}

		if (statusFilter !== 'all') {
			filtered = filtered.filter((article) => article.status === statusFilter);
		}

		return filtered;
	});

	function handleSearch() {
		const url = new URL($page.url);
		if (searchQuery.trim()) {
			url.searchParams.set('search', searchQuery);
		} else {
			url.searchParams.delete('search');
		}
		url.searchParams.set('page', '0'); // Reset to first page
		goto(url.toString());
	}

	function changePage(newPage: number) {
		const url = new URL($page.url);
		url.searchParams.set('page', newPage.toString());
		goto(url.toString());
	}

	function changeSort(newSort: string) {
		const url = new URL($page.url);
		url.searchParams.set('sort', newSort);
		url.searchParams.set('page', '0'); // Reset to first page
		goto(url.toString());
	}
</script>

<svelte:head>
	<title>Articles - AIrTicle</title>
	<meta
		name="description"
		content="Manage and review all articles in the AIrTicle validation system"
	/>
</svelte:head>

<div class="space-y-6">
	<!-- Header -->
	<div class="flex items-center justify-between">
		<div>
			<h1 class="text-3xl font-bold">Articles</h1>
			<p class="text-muted-foreground">Manage and review your articles with AI-powered analysis</p>
		</div>
		<Button href="/articles/new">
			<PlusIcon class="mr-2 h-4 w-4" />
			New Article
		</Button>
	</div>

	<!-- Search and Filters -->
	<div class="flex flex-col gap-4 sm:flex-row">
		<div class="relative flex-1">
			<SearchIcon
				class="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform"
			/>
			<Input
				bind:value={searchQuery}
				placeholder="Search articles..."
				class="pl-10"
				onkeydown={(e) => e.key === 'Enter' && handleSearch()}
			/>
		</div>

		<div class="flex gap-2">
			<select
				bind:value={statusFilter}
				class="border-input bg-background rounded-md border px-3 py-2 text-sm"
			>
				<option value="all">All Status</option>
				<option value="DRAFT">Draft</option>
				<option value="UNDER_AI_REVIEW">Under Review</option>
				<option value="APPROVED">Approved</option>
				<option value="REJECTED">Rejected</option>
				<option value="PUBLISHED">Published</option>
			</select>

			<select
				value={sort}
				onchange={(e) => changeSort(e.currentTarget.value)}
				class="border-input bg-background rounded-md border px-3 py-2 text-sm"
			>
				<option value="createdAt,desc">Newest First</option>
				<option value="createdAt,asc">Oldest First</option>
				<option value="title,asc">Title A-Z</option>
				<option value="title,desc">Title Z-A</option>
				<option value="status,asc">Status</option>
			</select>
		</div>
	</div>

	<!-- Articles Grid -->
	{#if filteredArticles().length === 0}
		<div class="space-y-4 py-12 text-center">
			<FilterIcon class="text-muted-foreground mx-auto h-16 w-16" />
			<h3 class="text-xl font-semibold">No articles found</h3>
			<p class="text-muted-foreground">
				{searchQuery.trim() || statusFilter !== 'all'
					? 'Try adjusting your search or filters'
					: 'Create your first article to get started'}
			</p>
			{#if !searchQuery.trim() && statusFilter === 'all'}
				<Button href="/articles/new">
					<PlusIcon class="mr-2 h-4 w-4" />
					Create First Article
				</Button>
			{/if}
		</div>
	{:else}
		<div class="grid gap-6">
			{#each filteredArticles() as article (article.id)}
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
								{#if article.updatedAt !== article.createdAt}
									<span>•</span>
									<span>Updated {formatDate(article.updatedAt)}</span>
								{/if}
							</div>

							{#if article.feedback}
								<div class="bg-muted/50 rounded-md p-3">
									<p class="mb-1 text-sm font-medium">AI Feedback:</p>
									<p class="text-muted-foreground text-sm">
										{truncateText(article.feedback, 150)}
									</p>
								</div>
							{/if}
						</div>

						{#if article.featuredImage}
							<img
								src={article.featuredImage}
								alt={article.title}
								class="ml-4 h-24 w-24 rounded-md object-cover"
							/>
						{/if}
					</div>

					<!-- Actions -->
					<div class="mt-4 flex items-center gap-2 border-t pt-4">
						<Button href="/articles/{article.id}" variant="outline" size="sm">View Details</Button>
						{#if ['DRAFT', 'REJECTED'].includes(article.status)}
							<Button href="/articles/{article.id}/edit" variant="outline" size="sm">Edit</Button>
						{/if}
						{#if article.status === 'APPROVED'}
							<Button variant="outline" size="sm">Publish</Button>
						{/if}
					</div>
				</div>
			{/each}
		</div>

		<!-- Pagination -->
		{#if articles.totalPages > 1}
			<div class="flex items-center justify-between">
				<p class="text-muted-foreground text-sm">
					Showing {currentPage * pageSize + 1} to {Math.min(
						(currentPage + 1) * pageSize,
						articles.totalElements
					)}
					of {articles.totalElements} articles
				</p>

				<div class="flex items-center gap-2">
					<Button
						variant="outline"
						size="sm"
						disabled={currentPage === 0}
						onclick={() => changePage(currentPage - 1)}
					>
						<ChevronLeftIcon class="h-4 w-4" />
						Previous
					</Button>

					<div class="flex items-center gap-1">
						{#each Array.from({ length: Math.min(5, articles.totalPages) }, (_, i) => {
							const startPage = Math.max(0, Math.min(currentPage - 2, articles.totalPages - 5));
							return startPage + i;
						}) as pageNum}
							<Button
								variant={pageNum === currentPage ? 'default' : 'outline'}
								size="sm"
								onclick={() => changePage(pageNum)}
								class="h-8 w-8 p-0"
							>
								{pageNum + 1}
							</Button>
						{/each}
					</div>

					<Button
						variant="outline"
						size="sm"
						disabled={currentPage >= articles.totalPages - 1}
						onclick={() => changePage(currentPage + 1)}
					>
						Next
						<ChevronRightIcon class="h-4 w-4" />
					</Button>
				</div>
			</div>
		{/if}
	{/if}
</div>
