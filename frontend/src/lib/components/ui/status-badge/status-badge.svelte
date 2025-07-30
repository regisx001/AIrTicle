<script lang="ts">
	import { Badge } from '$lib/components/ui/badge';
	import type { ArticleStatus } from '$lib/types';
	import { getStatusColor, getStatusIcon } from '$lib/utils/format';

	let { status }: { status: ArticleStatus } = $props();

	const colorClass = $derived(getStatusColor(status));
	const icon = $derived(getStatusIcon(status));
</script>

<Badge class={`inline-flex items-center gap-1 ${colorClass} border`}>
	<span class="text-xs">{icon}</span>
	<span class="text-xs font-medium">{status.replace(/_/g, ' ')}</span>
</Badge>
