import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Download, FileText, PieChart, TrendingUp, Calendar, Loader2 } from 'lucide-react';
import PageHeader from '../../components/ui/PageHeader';
import { reportApi } from '../../api/reportApi';
import { exportFullReportPdf } from '../../utils/exportPdf';
import { exportToCsv } from '../../utils/exportCsv';

export default function ReportsPage() {
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [isExporting, setIsExporting] = useState(false);

  const { data: summaryData, isLoading: isLoadingSummary } = useQuery({
    queryKey: ['reportSummary', dateFrom, dateTo],
    queryFn: () => reportApi.getSummary({ startDate: dateFrom || undefined, endDate: dateTo || undefined }),
  });

  const { data: mineData, isLoading: isLoadingMine } = useQuery({
    queryKey: ['reportMine', dateFrom, dateTo],
    queryFn: () => reportApi.getMineProduction({ startDate: dateFrom || undefined, endDate: dateTo || undefined }),
  });

  const { data: mineralData, isLoading: isLoadingMineral } = useQuery({
    queryKey: ['reportMineral', dateFrom, dateTo],
    queryFn: () => reportApi.getMineralDistribution({ startDate: dateFrom || undefined, endDate: dateTo || undefined }),
  });

  const isLoading = isLoadingSummary || isLoadingMine || isLoadingMineral;

  const handleExportPdf = async () => {
    if (!summaryData?.data) return;
    setIsExporting(true);
    try {
      exportFullReportPdf({
        summary: summaryData.data,
        mineProduction: mineData?.data || [],
        mineralDistribution: mineralData?.data || [],
        dateFrom: dateFrom || undefined,
        dateTo: dateTo || undefined,
      });
    } finally {
      setIsExporting(false);
    }
  };

  const handleExportCsv = () => {
    if (!mineData?.data) return;
    const data = mineData.data.map((item: any) => ({
      'Mine Name': item.mineName,
      'Total Batches': item.totalBatches,
      'Total Weight (kg)': item.totalWeight,
      'Avg Weight / Batch (kg)': item.totalBatches > 0 ? Math.round(item.totalWeight / item.totalBatches) : 0,
    }));
    exportToCsv(data, 'mine-production-report');
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Reports & Analytics"
        subtitle="Generate comprehensive reports on mineral production and supply chain metrics."
        action={
          <button
            onClick={handleExportPdf}
            disabled={isLoading || isExporting || !summaryData?.data}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
          >
            {isExporting ? (
              <Loader2 className="h-4 w-4 mr-2 animate-spin" />
            ) : (
              <Download className="h-4 w-4 mr-2" />
            )}
            Export Full Report (PDF)
          </button>
        }
      />

      {/* Filters */}
      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div className="flex flex-col sm:flex-row gap-4 items-end">
          <div className="flex-1">
            <label className="block text-xs font-medium text-gray-700 mb-1">Date From</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Calendar className="h-4 w-4 text-gray-400" />
              </div>
              <input
                type="date"
                value={dateFrom}
                onChange={e => setDateFrom(e.target.value)}
                className="block w-full pl-10 border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-2"
              />
            </div>
          </div>
          <div className="flex-1">
            <label className="block text-xs font-medium text-gray-700 mb-1">Date To</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Calendar className="h-4 w-4 text-gray-400" />
              </div>
              <input
                type="date"
                value={dateTo}
                onChange={e => setDateTo(e.target.value)}
                className="block w-full pl-10 border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-2"
              />
            </div>
          </div>
          <button
            onClick={() => { setDateFrom(''); setDateTo(''); }}
            className="px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            Clear
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Executive Summary */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center bg-gray-50">
            <h3 className="text-lg font-medium text-gray-900 flex items-center">
              <FileText className="h-5 w-5 mr-2 text-primary-600" />
              Executive Summary
            </h3>
          </div>
          <div className="p-6">
            {isLoadingSummary ? (
              <div className="animate-pulse space-y-4">
                <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                <div className="h-4 bg-gray-200 rounded w-1/2"></div>
                <div className="h-4 bg-gray-200 rounded w-5/6"></div>
              </div>
            ) : summaryData?.data ? (
              <dl className="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
                <div>
                  <dt className="text-sm font-medium text-gray-500">Total Batches</dt>
                  <dd className="mt-1 text-3xl font-semibold text-gray-900">{summaryData.data.totalBatches}</dd>
                </div>
                <div>
                  <dt className="text-sm font-medium text-gray-500">Total Volume Tracked</dt>
                  <dd className="mt-1 text-3xl font-semibold text-gray-900">
                    {summaryData.data.totalWeight.toLocaleString()} <span className="text-lg text-gray-500 font-normal">kg</span>
                  </dd>
                </div>
                <div>
                  <dt className="text-sm font-medium text-gray-500">Active Mines</dt>
                  <dd className="mt-1 text-3xl font-semibold text-green-600">{summaryData.data.activeMines}</dd>
                </div>
                <div>
                  <dt className="text-sm font-medium text-gray-500">Flagged Batches</dt>
                  <dd className="mt-1 text-3xl font-semibold text-red-600">{summaryData.data.flaggedBatches}</dd>
                </div>
                {summaryData.data.totalMovements !== undefined && (
                  <div>
                    <dt className="text-sm font-medium text-gray-500">Total Movements</dt>
                    <dd className="mt-1 text-3xl font-semibold text-gray-900">{summaryData.data.totalMovements}</dd>
                  </div>
                )}
                {summaryData.data.totalBatches > 0 && (
                  <div className="sm:col-span-2">
                    <dt className="text-sm font-medium text-gray-500 mb-2">Compliance Rate</dt>
                    <div className="w-full bg-gray-200 rounded-full h-3">
                      <div
                        className="bg-green-500 h-3 rounded-full transition-all"
                        style={{ width: `${((summaryData.data.totalBatches - summaryData.data.flaggedBatches) / summaryData.data.totalBatches * 100).toFixed(0)}%` }}
                      />
                    </div>
                    <p className="text-sm text-gray-600 mt-1">
                      {((summaryData.data.totalBatches - summaryData.data.flaggedBatches) / summaryData.data.totalBatches * 100).toFixed(1)}% of batches are compliant
                    </p>
                  </div>
                )}
              </dl>
            ) : (
              <p className="text-gray-500 text-sm">No data available.</p>
            )}
          </div>
        </div>

        {/* Mine Production */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center bg-gray-50">
            <h3 className="text-lg font-medium text-gray-900 flex items-center">
              <TrendingUp className="h-5 w-5 mr-2 text-primary-600" />
              Mine Production
            </h3>
            <button
              onClick={handleExportCsv}
              disabled={isLoadingMine || !mineData?.data}
              className="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded text-primary-700 bg-primary-100 hover:bg-primary-200 disabled:opacity-50"
            >
              <Download className="h-3.5 w-3.5 mr-1" />
              CSV
            </button>
          </div>
          <div className="p-0">
            {isLoadingMine ? (
              <div className="p-6 animate-pulse space-y-3">
                <div className="h-4 bg-gray-200 rounded w-full"></div>
                <div className="h-4 bg-gray-200 rounded w-full"></div>
              </div>
            ) : mineData?.data && mineData.data.length > 0 ? (
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mine</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Batches</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Weight (kg)</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {mineData.data.map((mine: any, idx: number) => (
                    <tr key={idx} className="hover:bg-gray-50">
                      <td className="px-6 py-4 text-sm font-medium text-gray-900">{mine.mineName}</td>
                      <td className="px-6 py-4 text-sm text-gray-500 text-right">{mine.totalBatches}</td>
                      <td className="px-6 py-4 text-sm font-medium text-gray-900 text-right">{mine.totalWeight.toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <div className="p-6 text-gray-500 text-sm">No production data found.</div>
            )}
          </div>
        </div>

        {/* Mineral Distribution */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden lg:col-span-2">
          <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center bg-gray-50">
            <h3 className="text-lg font-medium text-gray-900 flex items-center">
              <PieChart className="h-5 w-5 mr-2 text-primary-600" />
              Mineral Distribution
            </h3>
          </div>
          <div className="p-0">
            {isLoadingMineral ? (
              <div className="p-6 animate-pulse space-y-3">
                <div className="h-4 bg-gray-200 rounded w-full"></div>
                <div className="h-4 bg-gray-200 rounded w-full"></div>
              </div>
            ) : mineralData?.data && mineralData.data.length > 0 ? (
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mineral Type</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Batches</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Total Weight (kg)</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">% of Total</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {mineralData.data.map((mineral: any, idx: number) => (
                    <tr key={idx} className="hover:bg-gray-50">
                      <td className="px-6 py-4 text-sm font-medium text-gray-900">{mineral.mineralType}</td>
                      <td className="px-6 py-4 text-sm text-gray-500 text-right">{mineral.totalBatches}</td>
                      <td className="px-6 py-4 text-sm font-medium text-gray-900 text-right">{mineral.totalWeight.toLocaleString()}</td>
                      <td className="px-6 py-4 text-sm text-gray-500 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <div className="w-20 bg-gray-200 rounded-full h-1.5">
                            <div className="bg-primary-600 h-1.5 rounded-full" style={{ width: `${mineral.percentage}%` }} />
                          </div>
                          <span>{mineral.percentage}%</span>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <div className="p-6 text-gray-500 text-sm">No mineral data found.</div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
