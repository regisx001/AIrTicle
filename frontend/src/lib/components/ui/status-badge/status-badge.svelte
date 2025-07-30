<script lang="ts">
	import { Badge } from '$lib/components/ui/badge';
	import type { ArticleStatus } from '$lib/types';
	import { getStatusColor, getStatusIcon } from '$lib/utils/format';

	let { status }: { status: ArticleStatus } = $props();

	const colorClass = $derived(getStatusColor(status));
	const IconComponent = $derived(getStatusIcon(status));
</script>

<Badge class={`inline-flex items-center gap-1.5 ${colorClass} border`}>
	<IconComponent class="h-3 w-3" />
	<span class="text-xs font-medium">{status.replace(/_/g, ' ')}</span>
</Badge>
