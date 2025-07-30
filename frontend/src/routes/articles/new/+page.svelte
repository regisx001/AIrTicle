<script lang="ts">
	import { enhance } from '$app/forms';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Textarea } from '$lib/components/ui/textarea';
	import { wordCount } from '$lib/utils/format';
	import ArrowLeftIcon from '@lucide/svelte/icons/arrow-left';
	import SaveIcon from '@lucide/svelte/icons/save';
	import EyeIcon from '@lucide/svelte/icons/eye';
	import XCircleIcon from '@lucide/svelte/icons/x-circle';
	import BotIcon from '@lucide/svelte/icons/bot';
	import AlertTriangleIcon from '@lucide/svelte/icons/alert-triangle';
	import CheckIcon from '@lucide/svelte/icons/check';

	let { form } = $props();

	// Form state using Svelte 5 runes
	let title = $state(form?.title || '');
	let content = $state(form?.content || '');
	let featuredImage = $state(form?.featuredImage || '');
	let isSubmitting = $state(false);

	// Derived states
	const currentWordCount = $derived(wordCount(content));
	const isValid = $derived(
		title.trim().length > 0 && content.trim().length > 0 && currentWordCount >= 50
	);

	// Preview state
	let showPreview = $state(false);
</script>

<svelte:head>
	<title>Create New Article - AIrTicle</title>
	<meta name="description" content="Create a new article for AI-powered validation and analysis" />
</svelte:head>

<div class="mx-auto max-w-4xl space-y-8 p-6">
	<!-- Header -->
	<div class="flex items-center gap-6">
		<Button href="/articles" variant="outline" size="sm">
			<ArrowLeftIcon class="mr-2 h-4 w-4" />
			Back to Articles
		</Button>
		<div class="space-y-2">
			<h1 class="text-3xl font-bold">Create New Article</h1>
			<p class="text-muted-foreground text-base leading-relaxed">
				Write your article and let AI analyze its quality, grammar, and SEO optimization
			</p>
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

	<!-- Form -->
	<form
		method="POST"
		action="?/create"
		use:enhance={() => {
			isSubmitting = true;
			return async ({ update }) => {
				await update();
				isSubmitting = false;
			};
		}}
		class="space-y-8"
	>
		<div class="grid gap-8 md:grid-cols-3">
			<!-- Main Content -->
			<div class="space-y-8 md:col-span-2">
				<!-- Title -->
				<div class="space-y-3">
					<label for="title" class="text-sm font-medium"> Article Title * </label>
					<Input
						id="title"
						name="title"
						bind:value={title}
						placeholder="Enter a compelling title for your article..."
						required
						class="py-3 text-lg"
					/>
					<p class="text-muted-foreground text-sm leading-relaxed">
						A clear, descriptive title helps with SEO and readability analysis
					</p>
				</div>

				<!-- Content -->
				<div class="space-y-3">
					<div class="flex items-center justify-between">
						<label for="content" class="text-sm font-medium"> Article Content * </label>
						<div class="text-muted-foreground flex items-center gap-4 text-sm">
							<span class={currentWordCount < 50 ? 'text-destructive' : 'text-muted-foreground'}>
								{currentWordCount} words {currentWordCount < 50 ? '(minimum 50)' : ''}
							</span>
							<Button
								type="button"
								variant="outline"
								size="sm"
								onclick={() => (showPreview = !showPreview)}
							>
								<EyeIcon class="mr-2 h-4 w-4" />
								{showPreview ? 'Edit' : 'Preview'}
							</Button>
						</div>
					</div>

					{#if showPreview}
						<div class="bg-background min-h-96 rounded-lg border p-6">
							<div class="prose prose-sm dark:prose-invert max-w-none">
								<h1>{title || 'Article Title'}</h1>
								{#each content.split('\n') as paragraph}
									{#if paragraph.trim()}
										<p>{paragraph}</p>
									{/if}
								{/each}
							</div>
						</div>
					{:else}
						<Textarea
							id="content"
							name="content"
							bind:value={content}
							placeholder="Write your article content here. The AI will analyze grammar, readability, SEO optimization, and originality..."
							required
							rows={20}
							class="min-h-96 p-4 font-mono text-sm"
						/>
					{/if}

					<p class="text-muted-foreground text-sm leading-relaxed">
						Write comprehensive content. The AI analyzes quality, grammar, SEO, and originality.
					</p>
				</div>
			</div>

			<!-- Sidebar -->
			<div class="space-y-8">
				<!-- Featured Image -->
				<div class="space-y-3">
					<label for="featuredImage" class="text-sm font-medium"> Featured Image URL </label>
					<Input
						id="featuredImage"
						name="featuredImage"
						type="url"
						bind:value={featuredImage}
						placeholder="https://example.com/image.jpg"
					/>
					<p class="text-muted-foreground text-sm leading-relaxed">
						Optional. Provide a URL to an image that represents your article.
					</p>

					{#if featuredImage.trim()}
						<div class="mt-4">
							<img
								src={featuredImage}
								alt="Featured preview"
								class="h-36 w-full rounded-lg border object-cover shadow-sm transition-shadow hover:shadow-md"
								onerror={(e) => ((e.currentTarget as HTMLImageElement).style.display = 'none')}
							/>
						</div>
					{/if}
				</div>

				<!-- Analysis Info -->
				<div class="bg-muted/50 space-y-4 rounded-xl border p-6">
					<div class="flex items-center gap-3">
						<BotIcon class="h-5 w-5" />
						<h3 class="text-base font-medium">AI Analysis</h3>
					</div>
					<div class="text-muted-foreground space-y-3 text-sm">
						<div class="flex items-center gap-2">
							<CheckIcon class="h-4 w-4 text-green-600" />
							<p>Content Quality & Structure</p>
						</div>
						<div class="flex items-center gap-2">
							<CheckIcon class="h-4 w-4 text-green-600" />
							<p>Grammar & Readability</p>
						</div>
						<div class="flex items-center gap-2">
							<CheckIcon class="h-4 w-4 text-green-600" />
							<p>SEO Optimization</p>
						</div>
						<div class="flex items-center gap-2">
							<CheckIcon class="h-4 w-4 text-green-600" />
							<p>Originality Assessment</p>
						</div>
					</div>
					<p class="text-muted-foreground text-sm leading-relaxed">
						Analysis starts automatically after submission and typically completes within 2-5
						seconds.
					</p>
				</div>

				<!-- Writing Tips -->
				<div class="bg-primary/5 border-primary/20 space-y-4 rounded-xl border p-6">
					<h3 class="text-base font-medium">💡 Writing Tips</h3>
					<ul class="text-muted-foreground space-y-2 text-sm leading-relaxed">
						<li>• Use clear, descriptive headings</li>
						<li>• Write in active voice</li>
						<li>• Include relevant keywords naturally</li>
						<li>• Keep paragraphs concise</li>
						<li>• Proofread before submitting</li>
					</ul>
				</div>
			</div>
		</div>

		<!-- Submit Button -->
		<div class="flex items-center justify-between border-t pt-8">
			<div class="text-muted-foreground flex items-center gap-3 text-base">
				{#if isValid}
					<EyeIcon class="h-5 w-5 text-green-600" />
					<span>Ready to submit for AI analysis</span>
				{:else}
					<AlertTriangleIcon class="h-5 w-5 text-yellow-600" />
					<span
						>Please complete all required fields ({currentWordCount < 50
							? 'minimum 50 words needed'
							: ''})</span
					>
				{/if}
			</div>

			<Button type="submit" disabled={!isValid || isSubmitting} size="lg" class="px-8 py-3">
				{#if isSubmitting}
					<div class="mr-3 h-4 w-4 animate-spin rounded-full border-b-2 border-white"></div>
					Creating Article...
				{:else}
					<SaveIcon class="mr-3 h-4 w-4" />
					Create & Analyze Article
				{/if}
			</Button>
		</div>
	</form>
</div>
