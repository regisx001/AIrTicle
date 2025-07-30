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

<div class="mx-auto max-w-6xl space-y-6">
	<!-- Header -->
	<div class="flex items-center gap-4">
		<Button href="/articles" variant="outline" size="sm">
			<ArrowLeftIcon class="mr-2 h-4 w-4" />
			Back to Articles
		</Button>
		<div class="flex-1">
			<div class="mb-2 flex items-center gap-3">
				<h1 class="text-3xl font-bold">{article.title}</h1>
				<StatusBadge status={article.status} />
			</div>
			<div class="text-muted-foreground flex items-center gap-4 text-sm">
				<span>Created {formatDate(article.createdAt)}</span>
				{#if article.updatedAt !== article.createdAt}
					<span>•</span>
					<span>Updated {formatDate(article.updatedAt)}</span>
				{/if}
				<span>•</span>
				<span>{wordCount(article.content)} words</span>
				<span>•</span>
				<span>{estimateReadingTime(article.content)}</span>
			</div>
		</div>
		<div class="flex items-center gap-2">
			{#if ['DRAFT', 'REJECTED'].includes(article.status)}
				<Button href="/articles/{article.id}/edit" variant="outline">
					<EditIcon class="mr-2 h-4 w-4" />
					Edit
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
				<Button type="submit" variant="outline" disabled={isAnalyzing}>
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

	<!-- Error Display -->
	{#if form?.error}
		<div
			class="bg-destructive/10 border-destructive/20 text-destructive rounded-md border px-4 py-3"
		>
			<div class="flex items-center gap-2">
				<XCircleIcon class="h-5 w-5" />
				<p class="font-medium">{form.error}</p>
			</div>
		</div>
	{/if}

	<div class="grid gap-6 lg:grid-cols-3">
		<!-- Main Content -->
		<div class="space-y-6 lg:col-span-2">
			<!-- Featured Image -->
			{#if article.featuredImage}
				<img
					src={article.featuredImage}
					alt={article.title}
					class="h-64 w-full rounded-lg border object-cover"
				/>
			{/if}

			<!-- Article Content -->
			<div class="prose prose-lg dark:prose-invert max-w-none">
				<div class="whitespace-pre-wrap">{article.content}</div>
			</div>

			<!-- Feedback -->
			{#if article.feedback}
				<div class="bg-muted/50 rounded-lg p-4">
					<div class="mb-2 flex items-center gap-2">
						<MessageSquareIcon class="h-5 w-5" />
						<h3 class="font-semibold">AI Feedback</h3>
					</div>
					<p class="text-muted-foreground whitespace-pre-wrap">{article.feedback}</p>
				</div>
			{/if}
		</div>

		<!-- Sidebar -->
		<div class="space-y-6">
			<!-- Analysis Results -->
			{#if analysisResult}
				<div class="bg-card rounded-lg border p-6">
					<div class="mb-4 flex items-center gap-2">
						<BarChart3Icon class="h-5 w-5" />
						<h3 class="font-semibold">AI Analysis Results</h3>
					</div>

					<!-- Decision -->
					<div class="mb-4">
						{#if analysisResult}
							{@const DecisionIcon = getDecisionIcon(analysisResult.decision)}
							<div class="mb-2 flex items-center gap-2">
								<DecisionIcon class="h-5 w-5 {getDecisionColor(analysisResult.decision)}" />
								<span class="font-medium {getDecisionColor(analysisResult.decision)}">
									{analysisResult.decision.replace(/_/g, ' ')}
								</span>
							</div>
						{/if}
						<div class="text-muted-foreground flex items-center gap-2 text-sm">
							<span>Confidence:</span>
							<span class="font-medium {getScoreColor(analysisResult.confidenceScore)}">
								{formatScore(analysisResult.confidenceScore)}/10
							</span>
						</div>
					</div>

					<!-- Scores -->
					<div class="mb-4 space-y-3">
						<div class="flex items-center justify-between">
							<span class="text-sm">Content Quality</span>
							<span
								class="font-medium {getScoreColor((analysisResult.readabilityScore * 10) / 10)}"
							>
								{formatScore(analysisResult.readabilityScore / 10)}/10
							</span>
						</div>
						<div class="flex items-center justify-between">
							<span class="text-sm">Grammar</span>
							<span class="font-medium {getScoreColor((analysisResult.grammarScore * 10) / 10)}">
								{formatScore(analysisResult.grammarScore / 10)}/10
							</span>
						</div>
						<div class="flex items-center justify-between">
							<span class="text-sm">SEO Score</span>
							<span class="font-medium {getScoreColor((analysisResult.seoScore * 10) / 10)}">
								{formatScore(analysisResult.seoScore / 10)}/10
							</span>
						</div>
						<div class="flex items-center justify-between">
							<span class="text-sm">Originality</span>
							<span
								class="font-medium {getScoreColor((analysisResult.originalityScore * 10) / 10)}"
							>
								{formatScore(analysisResult.originalityScore / 10)}/10
							</span>
						</div>
					</div>

					<!-- Analysis Details -->
					<Tabs value="analysis" class="w-full">
						<TabsList class="grid w-full grid-cols-2">
							<TabsTrigger value="analysis">Analysis</TabsTrigger>
							<TabsTrigger value="recommendations">Tips</TabsTrigger>
						</TabsList>

						<TabsContent value="analysis" class="mt-4">
							<div class="space-y-2 text-sm">
								<p class="font-medium">AI Analysis:</p>
								<p class="text-muted-foreground whitespace-pre-wrap">
									{analysisResult.aiAnalysis}
								</p>
							</div>
						</TabsContent>

						<TabsContent value="recommendations" class="mt-4">
							<div class="space-y-2 text-sm">
								<p class="font-medium">Recommendations:</p>
								<p class="text-muted-foreground whitespace-pre-wrap">
									{analysisResult.recommendations}
								</p>
							</div>
						</TabsContent>
					</Tabs>

					<!-- Analysis Metadata -->
					<div class="text-muted-foreground mt-4 space-y-1 border-t pt-4 text-xs">
						<div>Model: {analysisResult.aiModel}</div>
						<div>Analyzed: {formatDate(analysisResult.analyzedAt)}</div>
						<div>Processing: {analysisResult.processingTimeMs}ms</div>
					</div>
				</div>
			{:else}
				<div class="bg-card space-y-4 rounded-lg border p-6 text-center">
					<ClockIcon class="text-muted-foreground mx-auto h-12 w-12" />
					<div>
						<h3 class="mb-2 font-semibold">Analysis Pending</h3>
						<p class="text-muted-foreground mb-4 text-sm">
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
							<Button type="submit" size="sm" disabled={isAnalyzing}>
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
				<div class="bg-card rounded-lg border p-6">
					<div class="mb-4 flex items-center gap-2">
						<HistoryIcon class="h-5 w-5" />
						<h3 class="font-semibold">Analysis History</h3>
					</div>

					<div class="space-y-3">
						{#each analysisHistory.slice(0, 5) as history}
							<div class="flex items-start gap-3 border-b pb-3 last:border-b-0">
								<div class="bg-primary mt-2 h-2 w-2 flex-shrink-0 rounded-full"></div>
								<div class="flex-1 text-sm">
									<div class="mb-1 flex items-center gap-2">
										{#if history.fromStatus}
											<span class="text-muted-foreground">
												{history.fromStatus.replace(/_/g, ' ')}
											</span>
											<span class="text-muted-foreground">→</span>
										{/if}
										<span class="font-medium">
											{history.toStatus.replace(/_/g, ' ')}
										</span>
									</div>
									<p class="text-muted-foreground mb-1 text-xs">
										{history.reason}
									</p>
									<div class="text-muted-foreground flex items-center gap-2 text-xs">
										<span>{formatDate(history.createdAt)}</span>
										<span>•</span>
										<span>by {history.performedBy}</span>
										{#if history.confidenceScore}
											<span>•</span>
											<span>{formatScore(history.confidenceScore)}/10</span>
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
