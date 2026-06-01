import { RWANDA_PROVINCES, getDistricts } from '../../constants/rwandaLocations';

interface LocationSelectProps {
  value: string;
  onChange: (value: string) => void;
  error?: string;
  label?: string;
  required?: boolean;
  className?: string;
}

export default function LocationSelect({ value, onChange, error, label, required, className }: LocationSelectProps) {
  const allDistricts = RWANDA_PROVINCES.flatMap((province) =>
    getDistricts(province).map((district) => ({ district, province }))
  );

  return (
    <div className={className}>
      {label && (
        <label className="block text-sm font-medium text-gray-700">
          {label} {required && '*'}
        </label>
      )}
      <select
        title={label || 'Location'}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className={`mt-1 block w-full pl-3 pr-10 py-2 text-base border ${error ? 'border-red-300' : 'border-gray-300'} focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm rounded-md`}
      >
        <option value="">Select location (district)</option>
        {RWANDA_PROVINCES.map((province) => (
          <optgroup key={province} label={province}>
            {getDistricts(province).map((district) => (
              <option key={district} value={district}>{district}</option>
            ))}
          </optgroup>
        ))}
      </select>
      {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
    </div>
  );
}
