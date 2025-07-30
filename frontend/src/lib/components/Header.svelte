<script lang="ts">
	import { page } from '$app/stores';
	import { Button } from '$lib/components/ui/button';
	import { toggleMode } from 'mode-watcher';
	import SunIcon from '@lucide/svelte/icons/sun';
	import MoonIcon from '@lucide/svelte/icons/moon';
	import PlusIcon from '@lucide/svelte/icons/plus';
	import HomeIcon from '@lucide/svelte/icons/home';
	import FileTextIcon from '@lucide/svelte/icons/file-text';

	const navigation = [
		{ name: 'Dashboard', href: '/', icon: HomeIcon },
		{ name: 'Articles', href: '/articles', icon: FileTextIcon }
	];
</script>

<header class="bg-background/95 supports-[backdrop-filter]:bg-background/60 border-b backdrop-blur">
	<div class="container mx-auto px-4">
		<div class="flex h-16 items-center justify-between">
			<!-- Logo -->
			<div class="flex items-center space-x-4">
				<a href="/" class="flex items-center space-x-2">
					<div
						class="bg-primary text-primary-foreground flex h-8 w-8 items-center justify-center rounded-md font-bold"
					>
						AI
					</div>
					<span class="text-xl font-bold">AIrTicle</span>
				</a>
			</div>

			<!-- Navigation -->
			<nav class="hidden items-center space-x-6 md:flex">
				{#each navigation as item}
					<a
						href={item.href}
						class="hover:text-primary flex items-center space-x-2 text-sm font-medium transition-colors {$page
							.url.pathname === item.href
							? 'text-primary'
							: 'text-muted-foreground'}"
					>
						<svelte:component this={item.icon} class="h-4 w-4" />
						<span>{item.name}</span>
					</a>
				{/each}
			</nav>

			<!-- Actions -->
			<div class="flex items-center space-x-4">
				<Button href="/articles/new" size="sm" class="hidden sm:flex">
					<PlusIcon class="mr-2 h-4 w-4" />
					New Article
				</Button>

				<Button onclick={toggleMode} variant="outline" size="icon">
					<SunIcon
						class="h-[1.2rem] w-[1.2rem] rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0"
					/>
					<MoonIcon
						class="absolute h-[1.2rem] w-[1.2rem] rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100"
					/>
					<span class="sr-only">Toggle theme</span>
				</Button>
			</div>
		</div>
	</div>
</header>
