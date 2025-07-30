<script lang="ts">
	import '../app.css';
	import { ModeWatcher } from 'mode-watcher';
	import Header from '$lib/components/Header.svelte';
	import { page } from '$app/stores';

	let { children } = $props();
</script>

<ModeWatcher />

<div class="bg-background min-h-screen">
	<Header />
	<main class="container mx-auto px-4 py-8">
		{@render children?.()}
	</main>
</div>
