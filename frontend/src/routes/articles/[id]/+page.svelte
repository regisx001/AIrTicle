<script lang="ts">
	import { enhance } from '$app/forms';
	import { Button } from '$lib/components/ui/button';
	import { StatusBadge } from '$lib/components/ui/status-badge';
	import { Tabs, TabsContent, TabsList, TabsTrigger } from '$lib/components/ui/tabs';
	import type { Article, AnalyseResult, AnalyseHistory } from '$lib/types';
	import {
		formatDate,
		formatScore,
		getScoreColor,
		wordCount,
		estimateReadingTime
	} from '$lib/utils/format';
	import ArrowLeftIcon from '@lucide/svelte/icons/arrow-left';
	import RefreshCwIcon from '@lucide/svelte/icons/refresh-cw';
	import EditIcon from '@lucide/svelte/icons/edit';
	import ClockIcon from '@lucide/svelte/icons/clock';
	import CheckCircleIcon from '@lucide/svelte/icons/check-circle';
	import XCircleIcon from '@lucide/svelte/icons/x-circle';
	import AlertCircleIcon from '@lucide/svelte/icons/alert-circle';
	import HistoryIcon from '@lucide/svelte/icons/history';
	import BarChart3Icon from '@lucide/svelte/icons/bar-chart-3';
	import MessageSquareIcon from '@lucide/svelte/icons/message-square';

	let { data, form } = $props();

	const article: Article = data.article;
	const analysisResult: AnalyseResult | null = data.analysisResult;
	const analysisHistory: AnalyseHistory[] = data.analysisHistory;

	let isAnalyzing = $state(false);

	function getDecisionIcon(decision: string) {
		switch (decision) {
			case 'APPROVED':
				return CheckCircleIcon;
			case 'REJECTED':
				return XCircleIcon;
			case 'MANUAL_REVIEW_REQUIRED':
				return AlertCircleIcon;
			default:
				return ClockIcon;
		}
	}

	function getDecisionColor(decision: string) {
		switch (decision) {
			case 'APPROVED':
				return 'text-green-600';
			case 'REJECTED':
				return 'text-red-600';
			case 'MANUAL_REVIEW_REQUIRED':
				return 'text-yellow-600';
			default:
				return 'text-gray-600';
		}
	}
</script>

<svelte:head>
	<title>{article.title} - AIrTicle</title>
	<meta name="description" content={article.content.substring(0, 160)} />
</svelte:head>

<div class="mx-auto max-w-6xl space-y-8 p-6">
	<!-- Header -->
	<div class="space-y-6">
		<!-- Navigation -->
		<div class="flex items-center justify-between">
			<Button href="/articles" variant="outline" size="sm">
				<ArrowLeftIcon class="mr-2 h-4 w-4" />
				Back to Articles
			</Button>

			<!-- Action Buttons -->
			<div class="flex items-center gap-3">
				{#if ['DRAFT', 'REJECTED'].includes(article.status)}
					<Button href="/articles/{article.id}/edit" variant="outline" size="sm">
						<EditIcon class="mr-2 h-4 w-4" />
						Edit Article
					</Button>
				{/if}

				<form
					method="POST"
					action="?/triggerAnalysis"
					use:enhance={() => {
						isAnalyzing = true;
						return async ({ update }) => {
							await update();
							isAnalyzing = false;
						};
					}}
				>
					<Button type="submit" variant="outline" size="sm" disabled={isAnalyzing}>
						{#if isAnalyzing}
							<div class="mr-2 h-4 w-4 animate-spin rounded-full border-b-2 border-current"></div>
							Analyzing...
						{:else}
							<RefreshCwIcon class="mr-2 h-4 w-4" />
							Re-analyze
						{/if}
					</Button>
				</form>
			</div>
		</div>

		<!-- Title & Status -->
		<div class="space-y-4">
			<div class="flex items-start gap-6">
				<div class="flex-1">
					<h1 class="text-3xl font-bold leading-tight sm:text-4xl">{article.title}</h1>
				</div>
				<div class="flex-shrink-0">
					<StatusBadge status={article.status} />
				</div>
			</div>

			<!-- Metadata -->
			<div class="text-muted-foreground flex flex-wrap items-center gap-6 text-sm">
				<div class="flex items-center gap-2">
					<ClockIcon class="h-4 w-4" />
					<span>Created {formatDate(article.createdAt)}</span>
				</div>
				{#if article.updatedAt !== article.createdAt}
					<div class="flex items-center gap-2">
						<EditIcon class="h-4 w-4" />
						<span>Updated {formatDate(article.updatedAt)}</span>
					</div>
				{/if}
				<div class="flex items-center gap-2">
					<span>{wordCount(article.content)} words</span>
				</div>
				<div class="flex items-center gap-2">
					<span>{estimateReadingTime(article.content)}</span>
				</div>
			</div>
		</div>
	</div>

	<!-- Error Display -->
	{#if form?.error}
		<div
			class="bg-destructive/10 border-destructive/20 text-destructive rounded-lg border px-6 py-4"
		>
			<div class="flex items-center gap-3">
				<XCircleIcon class="h-5 w-5" />
				<p class="font-medium">{form.error}</p>
			</div>
		</div>
	{/if}

	<div class="grid gap-8 lg:grid-cols-3">
		<!-- Main Content -->
		<div class="space-y-8 lg:col-span-2">
			<!-- Featured Image -->
			{#if article.featuredImage}
				<div class="overflow-hidden rounded-xl">
					<img
						src={article.featuredImage}
						alt={article.title}
						class="h-64 w-full object-cover transition-transform hover:scale-105"
					/>
				</div>
			{/if}

			<!-- Article Content -->
			<div class="prose prose-lg dark:prose-invert max-w-none">
				<div class="whitespace-pre-wrap leading-relaxed">{article.content}</div>
			</div>

			<!-- Feedback -->
			{#if article.feedback}
				<div class="bg-muted/50 rounded-xl p-6">
					<div class="mb-4 flex items-center gap-3">
						<MessageSquareIcon class="h-5 w-5" />
						<h3 class="text-lg font-semibold">AI Feedback</h3>
					</div>
					<p class="text-muted-foreground whitespace-pre-wrap leading-relaxed">
						{article.feedback}
					</p>
				</div>
			{/if}
		</div>

		<!-- Sidebar -->
		<div class="space-y-8">
			<!-- Analysis Results -->
			{#if analysisResult}
				<div class="bg-card rounded-xl border p-8">
					<div class="mb-6 flex items-center gap-3">
						<BarChart3Icon class="h-6 w-6" />
						<h3 class="text-lg font-semibold">AI Analysis Results</h3>
					</div>

					<!-- Decision -->
					<div class="mb-6">
						{#if analysisResult}
							{@const DecisionIcon = getDecisionIcon(analysisResult.decision)}
							<div class="mb-3 flex items-center gap-3">
								<DecisionIcon class="h-6 w-6 {getDecisionColor(analysisResult.decision)}" />
								<span class="text-lg font-semibold {getDecisionColor(analysisResult.decision)}">
									{analysisResult.decision.replace(/_/g, ' ')}
								</span>
							</div>
						{/if}
						<div class="text-muted-foreground flex items-center gap-3 text-sm">
							<span>Confidence:</span>
							<span class="font-semibold {getScoreColor(analysisResult.confidenceScore)}">
								{formatScore(analysisResult.confidenceScore)}/10
							</span>
						</div>
					</div>

					<!-- Scores -->
					<div class="mb-8 space-y-4">
						<div class="flex items-center justify-between py-2">
							<span class="font-medium">Content Quality</span>
							<span
								class="font-semibold {getScoreColor((analysisResult.readabilityScore * 10) / 10)}"
							>
								{formatScore((analysisResult.readabilityScore * 10) / 10)}/10
							</span>
						</div>
						<div class="flex items-center justify-between py-2">
							<span class="font-medium">Grammar</span>
							<span class="font-semibold {getScoreColor((analysisResult.grammarScore * 10) / 10)}">
								{formatScore((analysisResult.grammarScore * 10) / 10)}/10
							</span>
						</div>
						<div class="flex items-center justify-between py-2">
							<span class="font-medium">SEO Score</span>
							<span class="font-semibold {getScoreColor((analysisResult.seoScore * 10) / 10)}">
								{formatScore((analysisResult.seoScore * 10) / 10)}/10
							</span>
						</div>
						<div class="flex items-center justify-between py-2">
							<span class="font-medium">Originality</span>
							<span
								class="font-semibold {getScoreColor((analysisResult.originalityScore * 10) / 10)}"
							>
								{formatScore((analysisResult.originalityScore * 10) / 10)}/10
							</span>
						</div>
					</div>

					<!-- Analysis Details -->
					<Tabs value="analysis" class="w-full">
						<TabsList class="mb-6 grid w-full grid-cols-2">
							<TabsTrigger value="analysis">Analysis</TabsTrigger>
							<TabsTrigger value="recommendations">Tips</TabsTrigger>
						</TabsList>

						<TabsContent value="analysis" class="mt-6">
							<div class="space-y-3">
								<p class="font-semibold">AI Analysis:</p>
								<p class="text-muted-foreground whitespace-pre-wrap leading-relaxed">
									{analysisResult.aiAnalysis}
								</p>
							</div>
						</TabsContent>

						<TabsContent value="recommendations" class="mt-6">
							<div class="space-y-3">
								<p class="font-semibold">Recommendations:</p>
								<p class="text-muted-foreground whitespace-pre-wrap leading-relaxed">
									{analysisResult.recommendations}
								</p>
							</div>
						</TabsContent>
					</Tabs>

					<!-- Analysis Metadata -->
					<div class="text-muted-foreground mt-8 space-y-2 border-t pt-6 text-sm">
						<div class="flex justify-between">
							<span>Model:</span>
							<span class="font-medium">{analysisResult.aiModel}</span>
						</div>
						<div class="flex justify-between">
							<span>Analyzed:</span>
							<span class="font-medium">{formatDate(analysisResult.analyzedAt)}</span>
						</div>
						<div class="flex justify-between">
							<span>Processing:</span>
							<span class="font-medium">{analysisResult.processingTimeMs}ms</span>
						</div>
					</div>
				</div>
			{:else}
				<div class="bg-card space-y-6 rounded-xl border p-8 text-center">
					<ClockIcon class="text-muted-foreground mx-auto h-16 w-16" />
					<div class="space-y-4">
						<h3 class="text-xl font-semibold">Analysis Pending</h3>
						<p class="text-muted-foreground text-base leading-relaxed">
							{#if article.status === 'UNDER_AI_REVIEW'}
								AI analysis is currently in progress. Results will appear shortly.
							{:else}
								No analysis results available yet. Trigger a manual analysis to get AI insights.
							{/if}
						</p>

						<form
							method="POST"
							action="?/triggerAnalysis"
							use:enhance={() => {
								isAnalyzing = true;
								return async ({ update }) => {
									await update();
									isAnalyzing = false;
								};
							}}
						>
							<Button type="submit" size="lg" disabled={isAnalyzing} class="mt-4">
								{#if isAnalyzing}
									<div
										class="mr-2 h-4 w-4 animate-spin rounded-full border-b-2 border-current"
									></div>
									Analyzing...
								{:else}
									<RefreshCwIcon class="mr-2 h-4 w-4" />
									Analyze Now
								{/if}
							</Button>
						</form>
					</div>
				</div>
			{/if}

			<!-- Analysis History -->
			{#if analysisHistory.length > 0}
				<div class="bg-card rounded-xl border p-8">
					<div class="mb-6 flex items-center gap-3">
						<HistoryIcon class="h-6 w-6" />
						<h3 class="text-lg font-semibold">Analysis History</h3>
					</div>

					<div class="space-y-6">
						{#each analysisHistory.slice(0, 5) as history}
							<div class="flex items-start gap-4 border-b pb-6 last:border-b-0 last:pb-0">
								<div class="bg-primary mt-3 h-3 w-3 flex-shrink-0 rounded-full"></div>
								<div class="flex-1">
									<div class="mb-2 flex items-center gap-3">
										{#if history.fromStatus}
											<span class="text-muted-foreground font-medium">
												{history.fromStatus.replace(/_/g, ' ')}
											</span>
											<span class="text-muted-foreground">→</span>
										{/if}
										<span class="font-semibold">
											{history.toStatus.replace(/_/g, ' ')}
										</span>
									</div>
									<p class="text-muted-foreground mb-3 leading-relaxed">
										{history.reason}
									</p>
									<div class="text-muted-foreground flex flex-wrap items-center gap-3 text-sm">
										<span>{formatDate(history.createdAt)}</span>
										<span>•</span>
										<span>by {history.performedBy}</span>
										{#if history.confidenceScore}
											<span>•</span>
											<span>Score: {formatScore(history.confidenceScore)}/10</span>
										{/if}
									</div>
								</div>
							</div>
						{/each}
					</div>
				</div>
			{/if}
		</div>
	</div>
</div>
