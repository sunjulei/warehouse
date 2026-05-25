export interface DateRange {
  startDate: string
  endDate: string
}

const pad = (n: number) => String(n).padStart(2, '0')

const fmt = (d: Date): string => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`

export function getDateRange(type: string): DateRange {
  const now = new Date()
  let start: Date
  let end: Date

  switch (type) {
    case 'today':
      start = new Date(now)
      end = new Date(now)
      break
    case 'yesterday':
      start = new Date(now)
      start.setDate(start.getDate() - 1)
      end = new Date(start)
      break
    case 'week': {
      const day = now.getDay() || 7
      start = new Date(now)
      start.setDate(now.getDate() - day + 1)
      end = new Date(now)
      break
    }
    case 'month':
      start = new Date(now.getFullYear(), now.getMonth(), 1)
      end = new Date(now)
      break
    case 'quarter': {
      const q = Math.floor(now.getMonth() / 3)
      start = new Date(now.getFullYear(), q * 3, 1)
      end = new Date(now)
      break
    }
    case 'year':
      start = new Date(now.getFullYear(), 0, 1)
      end = new Date(now)
      break
    default:
      start = new Date(now)
      end = new Date(now)
  }

  return { startDate: fmt(start), endDate: fmt(end) }
}
