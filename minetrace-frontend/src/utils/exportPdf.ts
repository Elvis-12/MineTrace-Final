import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

// Brand colours
const PRIMARY_DARK  = [15,  40,  80]  as [number, number, number]; // #0f2850
const PRIMARY       = [30,  58,  138] as [number, number, number]; // #1e3a8a
const ACCENT        = [59,  130, 246] as [number, number, number]; // #3b82f6
const LIGHT_GRAY    = [248, 249, 250] as [number, number, number];
const MID_GRAY      = [107, 114, 128] as [number, number, number];
const DARK          = [17,  24,  39]  as [number, number, number];
const WHITE         = [255, 255, 255] as [number, number, number];
const RED           = [220, 38,  38]  as [number, number, number];
const GREEN         = [22,  163, 74]  as [number, number, number];
const AMBER         = [217, 119, 6]   as [number, number, number];

function addHeader(doc: jsPDF, title: string, subtitle: string, dateRange?: string) {
  const pageW = doc.internal.pageSize.getWidth();

  // Top bar
  doc.setFillColor(...PRIMARY_DARK);
  doc.rect(0, 0, pageW, 28, 'F');

  // Logo box
  doc.setFillColor(...ACCENT);
  doc.roundedRect(10, 5, 18, 18, 2, 2, 'F');
  doc.setTextColor(...WHITE);
  doc.setFontSize(11);
  doc.setFont('helvetica', 'bold');
  doc.text('MT', 19, 16.5, { align: 'center' });

  // Title
  doc.setFontSize(16);
  doc.setFont('helvetica', 'bold');
  doc.text('MineTrace', 32, 12);
  doc.setFontSize(8);
  doc.setFont('helvetica', 'normal');
  doc.text('Mineral Traceability Management System', 32, 18);

  // Case study tag
  doc.setFontSize(7);
  doc.setTextColor(...ACCENT);
  doc.text('Case Study: EAMITRACO', 32, 24);

  // Report date (top right)
  doc.setTextColor(...WHITE);
  doc.setFontSize(7);
  doc.setFont('helvetica', 'normal');
  const now = new Date().toLocaleString('en-GB', { dateStyle: 'medium', timeStyle: 'short' });
  doc.text(`Generated: ${now}`, pageW - 10, 10, { align: 'right' });
  if (dateRange) {
    doc.text(`Period: ${dateRange}`, pageW - 10, 17, { align: 'right' });
  }

  // Report title section
  doc.setFillColor(...LIGHT_GRAY);
  doc.rect(0, 28, pageW, 22, 'F');
  doc.setTextColor(...DARK);
  doc.setFontSize(14);
  doc.setFont('helvetica', 'bold');
  doc.text(title, 14, 40);
  doc.setFontSize(9);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(...MID_GRAY);
  doc.text(subtitle, 14, 47);

  // Separator line
  doc.setDrawColor(...ACCENT);
  doc.setLineWidth(0.8);
  doc.line(0, 50, pageW, 50);
}

function addFooter(doc: jsPDF, pageNum: number, totalPages: number) {
  const pageW = doc.internal.pageSize.getWidth();
  const pageH = doc.internal.pageSize.getHeight();

  doc.setDrawColor(...MID_GRAY);
  doc.setLineWidth(0.3);
  doc.line(10, pageH - 14, pageW - 10, pageH - 14);

  doc.setFontSize(7);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(...MID_GRAY);
  doc.text('MineTrace — Confidential. For internal use only.', 14, pageH - 8);
  doc.text(`Page ${pageNum} of ${totalPages}`, pageW - 14, pageH - 8, { align: 'right' });
}

function addSectionTitle(doc: jsPDF, text: string, y: number): number {
  const pageW = doc.internal.pageSize.getWidth();
  doc.setFillColor(...PRIMARY);
  doc.rect(10, y, pageW - 20, 8, 'F');
  doc.setTextColor(...WHITE);
  doc.setFontSize(9);
  doc.setFont('helvetica', 'bold');
  doc.text(text.toUpperCase(), 14, y + 5.5);
  return y + 12;
}

function addStatBox(doc: jsPDF, label: string, value: string, x: number, y: number, w: number, color: [number, number, number] = PRIMARY) {
  doc.setFillColor(...LIGHT_GRAY);
  doc.roundedRect(x, y, w, 20, 2, 2, 'F');
  doc.setDrawColor(...color);
  doc.setLineWidth(0.5);
  doc.line(x, y, x, y + 20);
  doc.setLineWidth(0.3);

  doc.setFontSize(7);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(...MID_GRAY);
  doc.text(label.toUpperCase(), x + 4, y + 7);

  doc.setFontSize(14);
  doc.setFont('helvetica', 'bold');
  doc.setTextColor(...color);
  doc.text(value, x + 4, y + 16);
}

export interface ReportData {
  summary: {
    totalBatches: number;
    totalWeight: number;
    activeMines: number;
    flaggedBatches: number;
    highRiskBatches?: number;
    totalMovements?: number;
  };
  mineProduction: Array<{ mineName: string; totalBatches: number; totalWeight: number }>;
  mineralDistribution: Array<{ mineralType: string; totalBatches: number; totalWeight: number; percentage: number }>;
  dateFrom?: string;
  dateTo?: string;
}

export const exportFullReportPdf = (data: ReportData) => {
  const doc = new jsPDF({ unit: 'mm', format: 'a4' });
  const pageW = doc.internal.pageSize.getWidth();

  const dateRange = data.dateFrom || data.dateTo
    ? `${data.dateFrom || 'Start'} → ${data.dateTo || 'Present'}`
    : undefined;

  // ── PAGE 1 ──────────────────────────────────────────────────────────────
  addHeader(doc, 'Executive Summary Report', 'Mineral traceability overview — supply chain performance and risk indicators', dateRange);

  // Stat boxes row
  const boxY = 58;
  const boxW = (pageW - 28) / 4 - 2;
  addStatBox(doc, 'Total Batches',   String(data.summary.totalBatches),                          10,             boxY, boxW, PRIMARY);
  addStatBox(doc, 'Total Volume',    `${data.summary.totalWeight.toLocaleString()} kg`,           10 + boxW + 2,  boxY, boxW, PRIMARY);
  addStatBox(doc, 'Active Mines',    String(data.summary.activeMines),                            10 + (boxW+2)*2, boxY, boxW, GREEN);
  addStatBox(doc, 'Flagged Batches', String(data.summary.flaggedBatches),                         10 + (boxW+2)*3, boxY, boxW, RED);

  // Extra stat row
  if (data.summary.highRiskBatches !== undefined || data.summary.totalMovements !== undefined) {
    const box2W = (pageW - 28) / 2 - 1;
    if (data.summary.highRiskBatches !== undefined) {
      addStatBox(doc, 'High Risk Batches', String(data.summary.highRiskBatches), 10, boxY + 24, box2W, RED);
    }
    if (data.summary.totalMovements !== undefined) {
      addStatBox(doc, 'Total Movements', String(data.summary.totalMovements), 10 + box2W + 2, boxY + 24, box2W, AMBER);
    }
  }

  // Compliance note
  let y = boxY + 52;
  y = addSectionTitle(doc, 'Compliance & Risk Overview', y);
  doc.setFontSize(9);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(...DARK);
  const flagRate = data.summary.totalBatches > 0
    ? ((data.summary.flaggedBatches / data.summary.totalBatches) * 100).toFixed(1)
    : '0.0';
  const compRate = data.summary.totalBatches > 0
    ? (((data.summary.totalBatches - data.summary.flaggedBatches) / data.summary.totalBatches) * 100).toFixed(1)
    : '100.0';

  doc.text(`• Compliance rate: ${compRate}% of batches have no fraud flags.`, 14, y);
  doc.text(`• Flag rate: ${flagRate}% of batches are flagged for review.`, 14, y + 6);
  doc.text(`• AI-powered Isolation Forest anomaly detection is applied to all registered batches.`, 14, y + 12);
  doc.text(`• All movements and verifications are recorded with full audit trail.`, 14, y + 18);

  // Risk indicator bar
  y += 28;
  doc.setFontSize(8);
  doc.setFont('helvetica', 'bold');
  doc.setTextColor(...DARK);
  doc.text('Overall Compliance Score', 14, y);
  y += 4;
  const barW = pageW - 28;
  doc.setFillColor(220, 38, 38);
  doc.rect(14, y, barW, 6, 'F');
  doc.setFillColor(...GREEN);
  doc.rect(14, y, barW * (parseFloat(compRate) / 100), 6, 'F');
  doc.setFontSize(7);
  doc.setTextColor(...WHITE);
  doc.text(`${compRate}%`, 16, y + 4.5);

  addFooter(doc, 1, 2);

  // ── PAGE 2 ──────────────────────────────────────────────────────────────
  doc.addPage();
  addHeader(doc, 'Production & Distribution Report', 'Mine output breakdown and mineral type analysis', dateRange);

  y = 58;

  // Mine Production table
  y = addSectionTitle(doc, '1. Mine Production', y);
  if (data.mineProduction.length > 0) {
    autoTable(doc, {
      startY: y,
      head: [['Mine Name', 'Total Batches', 'Total Weight (kg)', 'Avg Weight / Batch (kg)']],
      body: data.mineProduction.map(m => [
        m.mineName,
        m.totalBatches,
        m.totalWeight.toLocaleString(),
        m.totalBatches > 0 ? Math.round(m.totalWeight / m.totalBatches).toLocaleString() : '0',
      ]),
      theme: 'grid',
      headStyles: { fillColor: PRIMARY, textColor: WHITE, fontStyle: 'bold', fontSize: 8 },
      bodyStyles: { fontSize: 8, textColor: DARK },
      alternateRowStyles: { fillColor: LIGHT_GRAY },
      columnStyles: {
        1: { halign: 'center' },
        2: { halign: 'right' },
        3: { halign: 'right' },
      },
      margin: { left: 10, right: 10 },
    });
    y = (doc as any).lastAutoTable.finalY + 10;
  } else {
    doc.setFontSize(8);
    doc.setTextColor(...MID_GRAY);
    doc.text('No mine production data available.', 14, y + 6);
    y += 14;
  }

  // Mineral Distribution table
  y = addSectionTitle(doc, '2. Mineral Distribution', y);
  if (data.mineralDistribution.length > 0) {
    autoTable(doc, {
      startY: y,
      head: [['Mineral Type', 'Total Batches', 'Total Weight (kg)', '% of Total Volume']],
      body: data.mineralDistribution.map(m => [
        m.mineralType,
        m.totalBatches,
        m.totalWeight.toLocaleString(),
        `${m.percentage}%`,
      ]),
      theme: 'grid',
      headStyles: { fillColor: PRIMARY, textColor: WHITE, fontStyle: 'bold', fontSize: 8 },
      bodyStyles: { fontSize: 8, textColor: DARK },
      alternateRowStyles: { fillColor: LIGHT_GRAY },
      columnStyles: {
        1: { halign: 'center' },
        2: { halign: 'right' },
        3: { halign: 'right' },
      },
      margin: { left: 10, right: 10 },
    });
    y = (doc as any).lastAutoTable.finalY + 10;
  } else {
    doc.setFontSize(8);
    doc.setTextColor(...MID_GRAY);
    doc.text('No mineral distribution data available.', 14, y + 6);
    y += 14;
  }

  // Signature block
  y = Math.max(y, doc.internal.pageSize.getHeight() - 55);
  doc.setDrawColor(...MID_GRAY);
  doc.setLineWidth(0.3);
  doc.line(10, y, pageW - 10, y);
  y += 8;
  doc.setFontSize(8);
  doc.setFont('helvetica', 'bold');
  doc.setTextColor(...DARK);
  doc.text('Authorized By', 14, y);
  doc.text('Date', pageW / 2, y);
  y += 12;
  doc.setDrawColor(...DARK);
  doc.line(14, y, 90, y);
  doc.line(pageW / 2, y, pageW - 14, y);
  y += 5;
  doc.setFontSize(7);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(...MID_GRAY);
  doc.text('Signature & Stamp', 14, y);
  doc.text('Date of Issue', pageW / 2, y);

  addFooter(doc, 2, 2);

  const filename = `MineTrace-Report-${new Date().toISOString().slice(0, 10)}.pdf`;
  doc.save(filename);
};

// Keep original simple export for backwards compatibility
export const exportToPdf = (title: string, columns: string[], data: any[][], filename: string) => {
  const doc = new jsPDF();
  doc.setFontSize(18);
  doc.text(title, 14, 22);
  doc.setFontSize(11);
  doc.setTextColor(100);
  doc.text(`Generated on: ${new Date().toLocaleString()}`, 14, 30);
  autoTable(doc, {
    startY: 36,
    head: [columns],
    body: data,
    theme: 'grid',
    headStyles: { fillColor: PRIMARY },
  });
  doc.save(`${filename}.pdf`);
};

/**
 * Generic professional PDF for any table.
 * rows: array of objects — keys become columns.
 */
export const exportTablePdf = (
  title: string,
  subtitle: string,
  rows: Record<string, any>[],
  filename: string,
) => {
  if (!rows.length) return;
  const doc = new jsPDF({ unit: 'mm', format: 'a4', orientation: rows[0] && Object.keys(rows[0]).length > 6 ? 'landscape' : 'portrait' });

  addHeader(doc, title, subtitle);

  const columns = Object.keys(rows[0]);
  const body = rows.map(r => columns.map(c => r[c] ?? ''));

  autoTable(doc, {
    startY: 58,
    head: [columns],
    body,
    theme: 'grid',
    headStyles: { fillColor: PRIMARY_DARK, textColor: WHITE, fontStyle: 'bold', fontSize: 8 },
    bodyStyles: { fontSize: 7.5, textColor: DARK },
    alternateRowStyles: { fillColor: LIGHT_GRAY },
    margin: { left: 10, right: 10 },
    didDrawPage: (d: any) => {
      addFooter(doc, d.pageNumber, (doc as any).internal.getNumberOfPages());
    },
  });

  doc.save(`${filename}-${new Date().toISOString().slice(0, 10)}.pdf`);
};
