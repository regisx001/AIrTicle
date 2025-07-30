import type { ArticleStatus } from '$lib/types';
import EditIcon from '@lucide/svelte/icons/edit';
import SendIcon from '@lucide/svelte/icons/send';
import BotIcon from '@lucide/svelte/icons/bot';
import CheckCircleIcon from '@lucide/svelte/icons/check-circle';
import XCircleIcon from '@lucide/svelte/icons/x-circle';
import UserIcon from '@lucide/svelte/icons/user';
import GlobeIcon from '@lucide/svelte/icons/globe';
import ArchiveIcon from '@lucide/svelte/icons/archive';
import HelpCircleIcon from '@lucide/svelte/icons/help-circle';

export function getStatusColor(status: ArticleStatus): string {
	switch (status) {
		case 'DRAFT':
			return 'bg-gray-100 text-gray-800 border-gray-200';
		case 'SUBMITTED_FOR_APPROVAL':
			return 'bg-blue-100 text-blue-800 border-blue-200';
		case 'UNDER_AI_REVIEW':
			return 'bg-yellow-100 text-yellow-800 border-yellow-200';
		case 'AI_APPROVED':
		case 'APPROVED':
			return 'bg-green-100 text-green-800 border-green-200';
		case 'AI_REJECTED':
		case 'REJECTED':
			return 'bg-red-100 text-red-800 border-red-200';
		case 'MANUAL_REVIEW_REQUIRED':
			return 'bg-orange-100 text-orange-800 border-orange-200';
		case 'PUBLISHED':
			return 'bg-emerald-100 text-emerald-800 border-emerald-200';
		case 'ARCHIVED':
			return 'bg-slate-100 text-slate-800 border-slate-200';
		default:
			return 'bg-gray-100 text-gray-800 border-gray-200';
	}
}

export function getStatusIcon(status: ArticleStatus): typeof EditIcon {
	switch (status) {
		case 'DRAFT':
			return EditIcon;
		case 'SUBMITTED_FOR_APPROVAL':
			return SendIcon;
		case 'UNDER_AI_REVIEW':
			return BotIcon;
		case 'AI_APPROVED':
		case 'APPROVED':
			return CheckCircleIcon;
		case 'AI_REJECTED':
		case 'REJECTED':
			return XCircleIcon;
		case 'MANUAL_REVIEW_REQUIRED':
			return UserIcon;
		case 'PUBLISHED':
			return GlobeIcon;
		case 'ARCHIVED':
			return ArchiveIcon;
		default:
			return HelpCircleIcon;
	}
}

export function formatDate(dateString: string): string {
	return new Date(dateString).toLocaleDateString('en-US', {
		year: 'numeric',
		month: 'short',
		day: 'numeric',
		hour: '2-digit',
		minute: '2-digit'
	});
}

export function formatScore(score: number): string {
	return (score * 10).toFixed(1);
}

export function getScoreColor(score: number): string {
	if (score >= 0.8) return 'text-green-600';
	if (score >= 0.6) return 'text-yellow-600';
	return 'text-red-600';
}

export function truncateText(text: string, maxLength: number): string {
	if (text.length <= maxLength) return text;
	return text.substring(0, maxLength) + '...';
}

export function wordCount(text: string): number {
	return text.trim().split(/\s+/).filter(Boolean).length;
}

export function estimateReadingTime(text: string): string {
	const words = wordCount(text);
	const wordsPerMinute = 200;
	const minutes = Math.ceil(words / wordsPerMinute);
	return `${minutes} min read`;
}
